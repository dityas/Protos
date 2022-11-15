import json
import sys
import pandas


def load_json(fname):

    data = None

    with open(fname) as f:
        data = json.load(f)

    return data


def get_state_val(factors, varname):

    for f in factors:
        if varname in f.keys():
            return f

    return {}


def get_belief(trace, varname):

    i_bels = []
    j_bels = []
    states = []
    for t in trace:
        i_bel, j_bel = t["iBel"], t["jBel"]

        i_bel_ = get_state_val(i_bel, varname)
        j_bel_ = get_state_val(j_bel, varname)

        if len(i_bel_) > 0:
            i_bels.append(i_bel_)

        if len(j_bel_) > 0:
            j_bels.append(j_bel_)

        df = pandas.DataFrame({f"b_i({varname})": i_bels,
                               f"b_j({varname})": j_bels})

        print(df)

if __name__ == "__main__":
    data = load_json(sys.argv[1])

    for trace in data:
        get_belief(trace, sys.argv[2])
