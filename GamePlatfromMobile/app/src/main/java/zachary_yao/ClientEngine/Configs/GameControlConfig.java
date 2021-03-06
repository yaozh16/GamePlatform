package zachary_yao.ClientEngine.Configs;

import zachary_yao.ClientEngine.GameControler.GameControlerType;

public class GameControlConfig {
    private volatile GameControlerType gameControlerType=GameControlerType.DEFAULT;

    public void setGameControlerType(GameControlerType gameControlerType) {
        this.gameControlerType = gameControlerType;
    }

    public GameControlerType getGameControlerType() {
        return gameControlerType;
    }

}
