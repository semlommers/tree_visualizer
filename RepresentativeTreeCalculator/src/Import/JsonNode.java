package Import;

import com.google.gson.annotations.SerializedName;

public class JsonNode {

    @SerializedName("node_id")
    public Integer nodeId;
    public JsonChild[] children;
    @SerializedName("feature_id")
    public Integer featureId;
    @SerializedName("predicted_label")
    public Integer predictedLabel;

    public static class JsonChild {
        @SerializedName("child_id")
        public Integer childId;
        @SerializedName("min_value")
        public Double minValue;
        @SerializedName("max_value")
        public Double maxValue;
    }
}
