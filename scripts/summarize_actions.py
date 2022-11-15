import json
import sys
import pandas


def load_json(fname):

    data = None

    with open(fname) as f:
        data = json.load(f)

    return data


def get_joint_action(trace):

    i_actions = []
    i_obs_list = []
    j_actions = []
    j_obs_list = []
    i_obs_name = None
    j_obs_name = None
    for t in trace:
        i_act, j_act = t["iAct"], t["jAct"]
        i_obs, j_obs = t["iObs"], t["jObs"]

        if i_obs_name is None:
            i_obs_name = list(i_obs.keys())

        if j_obs_name is None:
            j_obs_name = list(j_obs.keys())

        i_obs_ = "|".join([i_obs[k] for k in i_obs_name])
        j_obs_ = "|".join([j_obs[k] for k in j_obs_name])

        i_actions.append(i_act)
        i_obs_list.append(i_obs_)
        j_actions.append(j_act)
        j_obs_list.append(j_obs_)

    df = pandas.DataFrame({"i_act": i_actions,
                           "|".join(i_obs_name): i_obs_list,
                           "j_act": j_actions,
                           "|".join(j_obs_name): j_obs_list})
    print(df)


if __name__ == "__main__":
    data = load_json(sys.argv[1])

    for trace in data:
        get_joint_action(trace)
