
package thinclab.domain_lang;

import java.util.ArrayList;

class DDLangList extends DDLangExpr {
    
    public final ArrayList<DDLangExpr> val;

    public DDLangList(ArrayList<DDLangExpr> val) {
        this.val = val;
    }
}
