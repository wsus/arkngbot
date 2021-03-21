package org.arkngbot.datastructures;

import java.io.Serializable;

public class TTCAutocompletionResult implements Serializable {

    private String value;

    private String label;

    private Integer itemId;

    private Integer itemCategory2Id;

    private Integer defaultQualityId;

    private String iconName;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public Integer getItemCategory2Id() {
        return itemCategory2Id;
    }

    public void setItemCategory2Id(Integer itemCategory2Id) {
        this.itemCategory2Id = itemCategory2Id;
    }

    public Integer getDefaultQualityId() {
        return defaultQualityId;
    }

    public void setDefaultQualityId(Integer defaultQualityId) {
        this.defaultQualityId = defaultQualityId;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
