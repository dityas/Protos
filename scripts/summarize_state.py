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

        if varname in i_bel.keys():
            i_bels.append(i_bel[varname][varname])

        if varname in j_bel.keys():
            j_bels.append(j_bel[varname][varname])

        df = pandas.DataFrame({f"b_i({varname})": i_bels,
                               f"b_j({varname})": j_bels})

        print(df)

if __name__ == "__main__":
    data = load_json(sys.argv[1])

    for trace in data:
        get_belief(trace, sys.argv[2])
