package id.tech.models;

/**
 * Created by macbook on 3/29/16.
 */
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PojoResponseRowCount {
    @SerializedName("json_code")
    @Expose
    private Integer jsonCode;
    @SerializedName("row_count")
    @Expose
    private Integer rowCount;

    /**
     *
     * @return
     * The jsonCode
     */
    public Integer getJsonCode() {
        return jsonCode;
    }

    /**
     *
     * @param jsonCode
     * The json_code
     */
    public void setJsonCode(Integer jsonCode) {
        this.jsonCode = jsonCode;
    }

    /**
     *
     * @return
     * The rowCount
     */
    public Integer getRowCount() {
        return rowCount;
    }

    /**
     *
     * @param rowCount
     * The row_count
     */
    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

}
