package com.stratagile.pnrouter.entity.events;

public class ChangEmailMenu {
    private String menu;
    public ChangEmailMenu(String menu) {
        this.menu = menu;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
