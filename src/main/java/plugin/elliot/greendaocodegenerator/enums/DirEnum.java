package plugin.elliot.greendaocodegenerator.enums;

import plugin.elliot.greendaocodegenerator.entity.MoudelLibrary;

public enum DirEnum {
    DATABASE(0, "dataBase"), ENTITY(1, "entity"), DAO(2, "dao"), MANAGER(3, "manager");

    /**
     * index
     */
    private Integer index;

    /**
     * title
     */
    private String type;


    public static DirEnum from(String instanceType) {
        for (DirEnum item : DirEnum.values()) {
            if (item.getType().equals(instanceType)) {
                return item;
            }
        }
        return null;
    }

    DirEnum(Integer index, String type) {
        this.index = index;
        this.type = type;
    }

    public Integer getIndex() {
        return index;
    }

    public String getType() {
        return type;
    }
}
