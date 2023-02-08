package plugin.elliot.greendaocodegenerator.entity;

import plugin.elliot.greendaocodegenerator.config.Constant;


public enum MoudelLibrary {

    /**
     * 转换类库
     */
    ENTITY(0, "Entity"), DAO(1, "Dao"), DAO_MASTER(4, "DaoMaster"), OTHER(3, "Other");

    public static MoudelLibrary from() {
        return from(Constant.sInstanceType);
    }

    private static MoudelLibrary from(String instanceType) {
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
