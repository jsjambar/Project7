public interface AIListener{
    void onResult(AIResponse result);
    void onError(AIError error);
    void onAudioLevel(float level);
    void onListeningStarted();
    void onListeningCanceled();
    void onListeningFinished();
}