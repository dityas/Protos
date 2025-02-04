package thinclab.domain_parser;


public class Cons {

    Object obj;
    Cons next;

    public Cons(Object obj, Cons next) {
        this.obj = obj;
        this.next = next;
    }

    public int length() {
         int l = 0;
         var head = this;

         while (head != null) {
             l += 1;
             head = head.next;
         }

         return l;
    }

    @Override
    public String toString() {
        var builder = new StringBuilder();
        builder.append("(");

        var head = this;
        while (head != null) {
            builder.append(head.obj.toString());

            if (head.next != null)
                builder.append(" ");

            head = head.next;
        }

        builder.append(")");

        return builder.toString();
    }
}
