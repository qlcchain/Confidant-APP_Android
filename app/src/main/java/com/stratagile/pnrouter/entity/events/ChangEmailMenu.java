package com.stratagile.pnrouter.entity.events;

public class ChangEmailMenu {
    private String name;
    private String menu;
    public ChangEmailMenu(String name,String menu) {
        this.name = name;
        this.menu = menu;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMenu() {
        return menu;
    }

    public void setMenu(String menu) {
        this.menu = menu;
    }
}
