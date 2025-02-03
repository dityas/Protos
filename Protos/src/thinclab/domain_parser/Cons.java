package thinclab.domain_parser;


public class Cons {

    Object obj;
    Cons next;

    public Cons(Object obj, Cons next) {
        this.obj = obj;
        this.next = next;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append("(");

        var head = this;
        while (head != null) {
            builder.append(head.obj.toString());

            if (head.obj instanceof Cons)
                builder.append("\r\n");
            else
                builder.append(" ");

            head = head.next;
        }

        builder.append(")");

        return builder.toString();
    }
}
