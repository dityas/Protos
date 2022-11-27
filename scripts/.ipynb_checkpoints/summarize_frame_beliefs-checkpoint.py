import json
import sys
import pandas


def load_json(fname):

    data = None

    with open(fname) as f:
        data = json.load(f)

    return data


def get_joint_action(trace):

    i_theta_hats = []
    j_theta_hats = []
    for t in trace:

        if "iThetaHat" in t.keys():
            i_theta_hats.append(t["iThetaHat"])

        if "jThetaHat" in t.keys():
            j_theta_hats.append(t["jThetaHat"])


    data = {}

    if len(i_theta_hats) > 0:
        data["i_theta_hat"] = i_theta_hats

    if len(j_theta_hats) > 0:
        data["j_theta_hat"] = j_theta_hats

    df = pandas.DataFrame(data)
    print(df)


if __name__ == "__main__":
    data = load_json(sys.argv[1])

    for trace in data:
        get_joint_action(trace)
