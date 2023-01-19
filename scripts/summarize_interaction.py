import json
import sys
import pandas
import numpy

from scipy.spatial import distance


def load_json(fname):

    data = None

    with open(fname) as f:
        data = json.load(f)

    return data


def pull_from_interaction(interaction, transform, key):
    
    data = transform(interaction)
    return data[key] if key in data.keys() else None


def pull_from_trace(trace, transform, key):
    return [pull_from_interaction(i, transform, key) for i in trace]


def pull_actions(trace):

    i_obs_keys = None
    j_obs_keys = None
    
    i_acts = pull_from_trace(trace, lambda x: x, "iAct")
    i_obs = pull_from_trace(trace, lambda x: x, "iObs")
    i_obs_keys = list(i_obs[0].keys()) if i_obs[0] is not None else None
    if i_obs_keys is not None:
        i_obs = list(map(lambda x: "|".join([x[k] for k in i_obs_keys]),
                         i_obs))

    j_acts = pull_from_trace(trace, lambda x: x, "jAct")
    j_obs = pull_from_trace(trace, lambda x: x, "jObs")
    j_obs_keys = list(j_obs[0].keys()) if j_obs[0] is not None else None
    if j_obs_keys is not None:
        j_obs = list(map(lambda x: "|".join([x[k] for k in j_obs_keys]),
                         j_obs))

    return pandas.DataFrame({"i_acts": i_acts, str(i_obs_keys): i_obs, 
                             "j_acts": j_acts, str(j_obs_keys): j_obs})


def pull_rewards(trace):

    i_Rs = pull_from_trace(trace, lambda x: x, "iR")
    j_Rs = pull_from_trace(trace, lambda x: x, "jR")

    return pandas.DataFrame({"i_Rs": i_Rs, "j_Rs": j_Rs})


def pull_frame_beliefs(trace):

    theta_hat_i = pull_from_trace(trace, lambda x: x, "iThetaHat")
    theta_hat_j = pull_from_trace(trace, lambda x: x, "jThetaHat")

    if theta_hat_i[0] is not None:
        key = list(theta_hat_i[0].keys())[0]
        frames = list(theta_hat_i[0][key].keys())

        theta_hat_i = list(map(lambda x: [x[key][f] for f in frames], 
                               theta_hat_i))

        theta_hat_i = pandas.DataFrame(data=theta_hat_i, columns=frames)

    if theta_hat_j[0] is not None:
        key = list(theta_hat_j[0].keys())[0]
        frames = list(theta_hat_j[0][key].keys())

        theta_hat_j = list(map(lambda x: [x[key][f] for f in frames], 
                               theta_hat_j))

        theta_hat_j = pandas.DataFrame(data=theta_hat_j, columns=frames)

    return (theta_hat_i, theta_hat_j)


def get_belief_keys(trace, var_name):

    try:
        keys = sorted(list(trace[0]["state"][var_name][var_name].keys()))
        return keys

    except Exception:
        return None


def get_all_var_keys(trace):

    keys = []
    for var_name in trace[0]["state"]:
        key = get_belief_keys(trace, var_name)

        if key is not None:
            keys.append((var_name, key))

    return keys


def pull_beliefs_for_agent(trace, agent_bel_key, var_key):

    if agent_bel_key not in trace[0].keys():
        return None

    b_list = pull_from_trace(trace,
                             lambda x: x[agent_bel_key],
                             var_key[0])

    bel_trace = map(lambda x: x[var_key[0]],
                    b_list)

    bel_array = map(lambda x: [x[k] for k in var_key[1]],
                    bel_trace)
    
    return numpy.array(list(bel_array))


def get_divergence(s, b):
    print(s.shape)
    print(b.shape)


def pull_belief(trace, var_key):
    """
        Returns a numpy array of dims ((s, b_i, b_j), len, n_vals)
    """

    b_is = pull_beliefs_for_agent(trace, "iBel", var_key)
    b_js = pull_beliefs_for_agent(trace, "jBel", var_key)
    s = pull_beliefs_for_agent(trace, "state", var_key)

    if b_js is not None:
        return numpy.array([s, b_is, b_js])
    else:
        return numpy.array([s, b_is])


def pull_all_beliefs(trace):

    data = []
    keys = []
    for key in get_all_var_keys(trace):
        keys.append(key)
        data.append(pull_belief(trace, key))

    return (keys, data)


def get_all_belief_traces(json_data):
    """
        Given  a JSON trace, outputs all beliefs as numpy arrays with dims
        (num_runs, (s, b_i, b_j), len, num_states)
    """

    keys = None
    data = None

    for trace in json_data:

        k, d = pull_all_beliefs(trace)

        if keys is None:
            keys = k
            data = [[] for _k in keys]

        for i in range(len(d)):
            data[i].append(d[i])

    data = [numpy.array(d) for d in data]

    return (keys, data)


def get_divergence(belief_trace):

    div_s_bi = distance.jensenshannon(belief_trace[:, 0, :, :],
                                      belief_trace[:, 1, :, :],
                                      axis=2)

    if belief_trace.shape[1] == 3:
        div_s_bj = distance.jensenshannon(belief_trace[:, 0, :, :],
                                          belief_trace[:, 2, :, :],
                                          axis=2)

        return (div_s_bi, div_s_bj)

    else:
        return (div_s_bi,)
    

def show_belief(trace, var_key):
    
    b_is = pull_beliefs_for_agent(trace, "iBel", var_key)
    b_js = pull_beliefs_for_agent(trace, "jBel", var_key)
    s = pull_beliefs_for_agent(trace, "state", var_key)
    
    s_frame = pandas.DataFrame(columns=var_key[1], data=s)
    b_is_frame = pandas.DataFrame(columns=var_key[1], data=b_is)
    
    if b_js is None:
        return {"state": s_frame, "agent_i": b_is_frame}
    else:
        b_js_frame = pandas.DataFrame(columns=var_key[1], data=b_js)
        return {"state:": s_frame, "agent_i": b_is_frame, 
                "agent_j": b_js_frame}


if __name__ == "__main__":
    data = load_json(sys.argv[1])
    k, d = get_all_belief_traces(data)

