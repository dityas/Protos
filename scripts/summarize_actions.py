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
    j_actions = []
    for t in trace:
        i_act, j_act = t["iAct"], t["jAct"]
        i_actions.append(i_act)
        j_actions.append(j_act)

    df = pandas.DataFrame({"i_act": i_actions, "j_act": j_actions})
    print(df)


if __name__ == "__main__":
    data = load_json(sys.argv[1])

    for trace in data:
        get_joint_action(trace)
