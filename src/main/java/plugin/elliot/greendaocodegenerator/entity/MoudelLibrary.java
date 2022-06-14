package plugin.elliot.greendaocodegenerator.entity;

import plugin.elliot.greendaocodegenerator.config.Constant;


public enum MoudelLibrary {

    /**
     * 转换类库
     */
    ENTITY(0, "Entity"), DAO(1, "Dao"), OTHER(2, "Other");

    public static MoudelLibrary from() {
        return from(Constant.sInstanceType);
    }

    private static MoudelLibrary from(String instanceType) {
        MoudelLibrary[] values = MoudelLibrary.values();
        for (MoudelLibrary item : MoudelLibrary.values()) {
            if (item.getType().equals(instanceType)) {
                return item;
            }
        }
        return null;
    }


    /**
     * index
     */
    private Integer index;

    /**
     * title
     */
    private String type;

    MoudelLibrary(Integer index, String type) {
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
