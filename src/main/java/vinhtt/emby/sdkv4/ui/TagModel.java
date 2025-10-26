package vinhtt.emby.sdkv4.ui; // Hoặc package bạn muốn

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import java.util.Objects;

public class TagModel {

    // Cần thêm thư viện Gson vào pom.xml nếu chưa có
    // <dependency>
    //     <groupId>com.google.code.gson</groupId>
    //     <artifactId>gson</artifactId>
    //     <version>2.10.1</version> // </dependency>
    private static final Gson gson = new Gson();

    private final boolean isJson;
    private final String simpleName;
    private final String key;
    private final String value;

    public TagModel(String simpleName) {
        this.isJson = false;
        this.simpleName = simpleName;
        this.key = null;
        this.value = null;
    }

    public TagModel(String key, String value) {
        this.isJson = true;
        this.simpleName = null;
        this.key = key;
        this.value = value;
    }

    public static TagModel parse(String rawName) {
        if (rawName == null || rawName.isEmpty()) {
            return new TagModel(""); // Trả về rỗng thay vì "Trống"
        }

        if (rawName.startsWith("{") && rawName.endsWith("}")) {
            try {
                JsonObject jsonObject = gson.fromJson(rawName, JsonObject.class);
                Map.Entry<String, com.google.gson.JsonElement> firstEntry = jsonObject.entrySet().stream().findFirst().orElse(null);
                if (firstEntry != null && firstEntry.getValue().isJsonPrimitive()) { // Kiểm tra là primitive
                    return new TagModel(firstEntry.getKey(), firstEntry.getValue().getAsString());
                } else {
                    System.err.println("Cấu trúc JSON không mong đợi hoặc value không phải primitive: " + rawName);
                }
            } catch (JsonSyntaxException | IllegalStateException e) {
                System.err.println("Lỗi parse JSON trong TagModel, coi là chuỗi thường: " + rawName + " - " + e.getMessage());
            }
        }
        return new TagModel(rawName);
    }

    public String serialize() {
        if (isJson) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty(key, value);
            return gson.toJson(jsonObject);
        } else {
            return simpleName;
        }
    }

    public String getDisplayName() {
        if (isJson) {
            return String.format("%s | %s", key, value);
        } else {
            return simpleName;
        }
    }

    public boolean isJson() {
        return isJson;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagModel tagModel = (TagModel) o;
        return isJson == tagModel.isJson && Objects.equals(simpleName, tagModel.simpleName) && Objects.equals(key, tagModel.key) && Objects.equals(value, tagModel.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(isJson, simpleName, key, value);
    }

    @Override
    public String toString() {
        return "TagModel{" + getDisplayName() + "}";
    }
}