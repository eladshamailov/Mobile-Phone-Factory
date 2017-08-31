package bgu.spl.a2.sim.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ToolJson {

    @SerializedName("tool")
    @Expose
    private String tool;
    @SerializedName("qty")
    @Expose
    private Integer qty;

    /**
     *
     * @return tool
     */
    public String getTool() {
        return tool;
    }

    /**
     *
     * @param tool
     */
    public void setTool(String tool) {
        this.tool = tool;
    }

    /**
     *
     * @return Qty
     */
    public Integer getQty() {
        return qty;
    }

    /**
     *
     * @param qty
     */
    public void setQty(Integer qty) {
        this.qty = qty;
    }
}