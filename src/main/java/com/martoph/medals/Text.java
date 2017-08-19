package com.martoph.medals;

public enum  Text {
    LOCKED(ConfigUtil.getString("text.locked")),
    SYNTAX(ConfigUtil.getString("text.syntax")),
    INVALIDMATERIAL(ConfigUtil.getString("text.invalidMaterial")),
    INVALIDMEDAL(ConfigUtil.getString("text.invalidMedal")),
    SUCCESSFULCREATE(ConfigUtil.getString("text.successfulCreate")),
    SUCCESSFULDELETE(ConfigUtil.getString("text.successfulDelete")),
    GUINAME(ConfigUtil.getString("text.guiname")),
    DESPAWN(ConfigUtil.getString("text.despawn")),
    NEXTPAGE(ConfigUtil.getString("text.nextpage")),
    PREVPAGE(ConfigUtil.getString("text.prevpage")),
    HIDDENFROMPLAYER(ConfigUtil.getString("text.hiddenFromPlayer")),
    HIDDENFROMPLAYERLORE(ConfigUtil.getString("text.hiddenFromPlayerLore")),
    SHOWNTOPLAYER(ConfigUtil.getString("text.shownToPlayer")),
    SHOWNTOPLAYERLORE(ConfigUtil.getString("text.shownToPlayerLore"));

    private String value;

    Text(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
