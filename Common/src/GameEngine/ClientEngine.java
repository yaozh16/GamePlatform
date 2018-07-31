package GameEngine;

public interface ClientEngine{
    public void start();
    public void finish();
    public void buildGridMapReader();
    public void buildOperationWriter();
    public ClientEngine setClientEngineHolder(ClientEngineHolder clientEngineHolder);
    public void clientEngineClear();

    public void notifyControlConfigChange();
}
