import json
import sys
import networkx
import matplotlib.pyplot as plotter
import graphviz


def load_json(fname):

    data = None

    with open(fname) as f:
        data = json.load(f)

    return data


def get_graphviz_graph(json_data):
    G = graphviz.Digraph()
    
    nodes = json_data["nodes"]
    edges = json_data["edges"]

    for k, v in nodes.items():
        G.node(k)
        
    return G


def to_networkx_graph(json_data):

    nodes = json_data["nodes"]
    edges = json_data["edges"]

    G = networkx.MultiDiGraph()

    for k, v in nodes.items():
        G.add_node(k, label=v)

    # networkx.draw(G)
    plotter.plot(list(range(100)))
    plotter.show()


if __name__ == "__main__":
    data = load_json(sys.argv[1])

    to_networkx_graph(data)
