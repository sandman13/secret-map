public enum TypeEnum {

    BACKGROUND("背景图片"),

    GAME_PROCESSING("游戏进行中"),

    WIN("胜利");

    public String description;


    TypeEnum(String description) {
        this.description = description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
