import json
import sys
import pandas


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

    i_acts = pull_from_trace(trace, lambda x: x, "iAct")
    j_acts = pull_from_trace(trace, lambda x: x, "jAct")

    return pandas.DataFrame({"i_acts": i_acts, "j_acts": j_acts})


def pull_rewards(trace):

    i_Rs = pull_from_trace(trace, lambda x: x, "iR")
    j_Rs = pull_from_trace(trace, lambda x: x, "jR")

    return pandas.DataFrame({"i_Rs": i_Rs, "j_Rs": j_Rs})


def pull_beliefs(trace, var_name):

    b_is = pull_from_trace(trace,
                           lambda x: x["iBel"] if "iBel" in x.keys() else x,
                           var_name)
    b_js = pull_from_trace(trace,
                           lambda x: x["jBel"] if "jBel" in x.keys() else x,
                           var_name)
    s = pull_from_trace(trace, lambda x: x["state"], var_name)

    print(b_is)
    print(b_js)
    print(s)

if __name__ == "__main__":
    data = load_json(sys.argv[1])

    for trace in data:
        actions = pull_actions(trace)
        rewards = pull_rewards(trace)

        print(actions)
        print(rewards)
        pull_beliefs(trace, "AccFound")
