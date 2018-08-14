package zachary_yao.ClientEngine.GameControler;

public enum  GameControlerType {
    DEFAULT,WASD,ULDR,NONE;
    public String toString(){
        switch (this){
            case NONE:
                return "NONE(放弃控制)";
            case DEFAULT:
                return "DEFAULT(WASD+上下左右控制)";
            case WASD:
                return "WASD(WASD控制)";
            case ULDR:
                return "上下左右(上下左右控制)";
            default:
                return null;
        }
    }
}
