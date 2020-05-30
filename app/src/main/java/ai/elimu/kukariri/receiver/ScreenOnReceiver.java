package ai.elimu.kukariri.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.hardware.display.DisplayManager;
import android.util.Log;
import android.view.Display;

import java.util.List;
import java.util.Set;

import ai.elimu.kukariri.MainActivity;
import ai.elimu.kukariri.assessment.WordAssessmentActivity;
import ai.elimu.kukariri.util.ReviewHelper;
import ai.elimu.model.v2.gson.analytics.WordAssessmentEventGson;
import ai.elimu.model.v2.gson.analytics.WordLearningEventGson;

public class ScreenOnReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(getClass().getName(), "onReceive");

        Log.i(getClass().getName(), "intent: " + intent);
        Log.i(getClass().getName(), "intent.getAction(): " + intent.getAction());

        // Do not proceed if the screen is not active
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        for (Display display : displayManager.getDisplays()) {
            Log.i(getClass().getName(), "display: " + display);
            if (display.getState() != Display.STATE_ON) {
                return;
            }
        }

        // Check if there are pending reviews
        // Get a list of the Words that have been previously learned
        List<WordLearningEventGson> wordLearningEventGsons = new WordAssessmentActivity().getWordLearningEventGsons(context);

        // Get a set of the Words that have been previously learned
        Set<Long> idsOfWordsInWordLearningEvents = new WordAssessmentActivity().getIdsOfWordsInWordLearningEvents(context);

        // Get a list of assessment events for the words that have been previously learned
        List<WordAssessmentEventGson> wordAssessmentEventGsons = new WordAssessmentActivity().getWordAssessmentEventGsons(idsOfWordsInWordLearningEvents, context);

        // Determine which of the previously learned Words are pending a review (based on WordAssessmentEvents)
        Set<Long> idsOfWordsPendingReview = ReviewHelper.getIdsOfWordsPendingReview(idsOfWordsInWordLearningEvents, wordLearningEventGsons, wordAssessmentEventGsons);
        Log.i(getClass().getName(), "idsOfWordsPendingReview.size(): " + idsOfWordsPendingReview.size());
        if (!idsOfWordsPendingReview.isEmpty()) {
            // Launch the application
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(launchIntent);
        }
    }
}
