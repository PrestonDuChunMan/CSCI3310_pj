package edu.cuhk.csci3310.basketball_app;

import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.os.CountDownTimer;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import android.widget.SeekBar;
import android.content.SharedPreferences; //preseve game when user exit app


public class GameStateTrackerActivity extends AppCompatActivity {

    // UI things
    private TextView homeScoreText, awayScoreText;
    private TextView gameTimeText, shotClockText;
    private Button homeAdd1, homeSubtract;
    private Button awayAdd1, awaySubtract;
    private Button startTimeBtn, pauseTimeBtn, resetShotClock24Btn, resetShotClock14Btn, resetGameBtn;

    // game details
    private int homeScore = 0;
    private int awayScore = 0;
    private boolean isGameTimerRunning = false;
    private boolean isShotClockRunning = false;
    private boolean shotClockExpired = false;
    private boolean gameClockExpired = false;

//    10 mins game time, 24 sec shot clock
    private long gameTimeLeftMillis = 10 * 60 * 1000;
    private long shotClockLeftMillis = 24 * 1000;
    private boolean isSoundEnabled = true;

    private CountDownTimer gameTimer;
    private CountDownTimer shotTimer;

    // buzzer sound
    private MediaPlayer shotClockBuzzer;
    private MediaPlayer gameClockBuzzer;

    private Typeface digitalFont;

    private static final long STANDARD_INTERVAL = 1000;
    private static final long DETAILED_INTERVAL = 100;

    //right side offcanvas
    private DrawerLayout drawerLayout;
    private SeekBar gameDurationSlider;
    private TextView gameDurationText;
    private Button applySettingsButton;
    private int selectedGameDuration = 10;

    //save game state
    private static final String PREFS_NAME = "BasketballGamePrefs";
    private static final String KEY_GAME_IN_PROGRESS = "gameInProgress";
    private static final String KEY_HOME_SCORE = "homeScore";
    private static final String KEY_AWAY_SCORE = "awayScore";
    private static final String KEY_GAME_TIME = "gameTimeLeft";
    private static final String KEY_SHOT_CLOCK = "shotClockLeft";
    private static final String KEY_GAME_DURATION = "gameDuration";
    private static final String KEY_SOUND_ENABLED = "soundEnabled";

    private static final String KEY_SHOT_CLOCK_DURATION = "shotClockDuration";
    private int shotClockDuration = 24;

    private int fullShotClockValue = 24;
    private int reducedShotClockValue = 14;

    //for touch feature
    private boolean isLandscapeMode = false;


    // Save the current game state to SharedPreferences
    private void saveGameState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        boolean gameInProgress = homeScore > 0 || awayScore > 0 ||
                gameTimeLeftMillis < selectedGameDuration * 60 * 1000;

        if (gameInProgress) {
            editor.putBoolean(KEY_GAME_IN_PROGRESS, true);
            editor.putInt(KEY_HOME_SCORE, homeScore);
            editor.putInt(KEY_AWAY_SCORE, awayScore);
            editor.putLong(KEY_GAME_TIME, gameTimeLeftMillis);
            editor.putLong(KEY_SHOT_CLOCK, shotClockLeftMillis);
            editor.putInt(KEY_GAME_DURATION, selectedGameDuration);
            editor.putBoolean(KEY_SOUND_ENABLED, isSoundEnabled);
            editor.putInt(KEY_SHOT_CLOCK_DURATION, shotClockDuration);
        } else {
            editor.clear();
        }

        editor.apply();
    }

    private boolean hasSavedGame() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(KEY_GAME_IN_PROGRESS, false);
    }

    // SharedPreferences load
    private void loadGameState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        homeScore = prefs.getInt(KEY_HOME_SCORE, 0);
        awayScore = prefs.getInt(KEY_AWAY_SCORE, 0);
        gameTimeLeftMillis = prefs.getLong(KEY_GAME_TIME, selectedGameDuration * 60 * 1000);
        shotClockDuration = prefs.getInt(KEY_SHOT_CLOCK_DURATION, 24);
        shotClockLeftMillis = prefs.getLong(KEY_SHOT_CLOCK, shotClockDuration * 1000);
        selectedGameDuration = prefs.getInt(KEY_GAME_DURATION, 10);
        isSoundEnabled = prefs.getBoolean(KEY_SOUND_ENABLED, true);

        if (shotClockDuration == 24) {
            fullShotClockValue = 24;
            reducedShotClockValue = 14;
        } else {
            fullShotClockValue = 30;
            reducedShotClockValue = 20;
        }

        updateHomeScore(homeScore);
        updateAwayScore(awayScore);
        updateGameTimeDisplay(gameTimeLeftMillis);
        updateShotClockDisplay(shotClockLeftMillis);

        updateShotClockButtonsText();
        updateShotClockButtonsListeners();

        shotClockExpired = false;
        gameClockExpired = gameTimeLeftMillis <= 0;

        if (gameClockExpired) {
            gameTimeText.setBackground(getResources().getDrawable(R.drawable.expired_clock_border, null));
        } else {
            gameTimeText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));
        }
        shotClockText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));

        // Check shot clock visibility
        checkAndUpdateShotClockVisibility();

        // Update settings drawer UI
        if (gameDurationSlider != null) {
            gameDurationSlider.setProgress(selectedGameDuration);
        }
        if (gameDurationText != null) {
            gameDurationText.setText(selectedGameDuration + " minutes");
        }

        // Update radio buttons to match loaded shot clock duration
        RadioButton shotClock24Button = findViewById(R.id.shot_clock_24);
        RadioButton shotClock30Button = findViewById(R.id.shot_clock_30);
        if (shotClock24Button != null && shotClock30Button != null) {
            if (shotClockDuration == 24) {
                shotClock24Button.setChecked(true);
            } else {
                shotClock30Button.setChecked(true);
            }
        }
    }

    // Clear saved game state
    private void clearSavedGameState() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_state_tracker);

        // Check if device is in landscape mode
        isLandscapeMode = getResources().getConfiguration().orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE;

        // Set up the toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Basketball Tracker");
        }

        // Load digital font
        try {
            digitalFont = ResourcesCompat.getFont(this, R.font.digital);
        } catch (Exception e) {
            e.printStackTrace();
        }

        initSoundPlayers();

        findViews();
        setupDigitalClocks();
        setupInitialValues();
        setupClickListeners();
        setupSettingsDrawer();

        if (isLandscapeMode) {
            setupTouchListeners();
        }

        if (hasSavedGame()) {
            promptToRestoreGame();
        }
    }

    private void setupTouchListeners() {
        // touch to add score
        homeScoreText.setOnClickListener(v -> {
            updateHomeScore(homeScore + 1);
        });

        // long touch to reduce score
        homeScoreText.setOnLongClickListener(v -> {
            updateHomeScore(homeScore - 1);

            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);

            return true;
        });

        awayScoreText.setOnClickListener(v -> {
            updateAwayScore(awayScore + 1);
        });

        awayScoreText.setOnLongClickListener(v -> {
            updateAwayScore(awayScore - 1);

            v.performHapticFeedback(android.view.HapticFeedbackConstants.LONG_PRESS);

            return true;
        });

        // touch to start/stop countdown
        gameTimeText.setOnClickListener(v -> {
            if (gameClockExpired) {
                return;
            }

            if (isGameTimerRunning) {
                pauseTimers();
            } else {
                if (shotClockExpired) {
                    shotClockLeftMillis = 24 * 1000;
                    updateShotClockDisplay(shotClockLeftMillis);
                    shotClockExpired = false;
                    shotClockText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));
                }
                startTimers();
            }
        });
    }

    // Also update the configuration change detection to reset the touch listeners
    @Override
    public void onConfigurationChanged(android.content.res.Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        boolean wasLandscape = isLandscapeMode;
        isLandscapeMode = newConfig.orientation ==
                android.content.res.Configuration.ORIENTATION_LANDSCAPE;

        if (wasLandscape != isLandscapeMode) {
            boolean wasRunning = isGameTimerRunning;
            if (wasRunning) {
                pauseTimers();
            }

            findViews();
            setupDigitalClocks();
            setupClickListeners();

            if (isLandscapeMode) {
                setupTouchListeners();
            }

            if (wasRunning) {
                startTimers();
            }
        }
    }

    private void promptToRestoreGame() {
        new AlertDialog.Builder(this)
                .setTitle("Game Paused")
                .setMessage("Do you want to continue your previous game?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    loadGameState();
                })
                .setNegativeButton("No", (dialog, which) -> {
                    clearSavedGameState();
                    resetGame();
                })
                .setCancelable(false)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.game_tracker_menu, menu);

        updateSoundMenuIcon(menu);

        return true;
    }

    private void updateSoundMenuIcon(Menu menu) {
        if (menu != null) {
            MenuItem soundItem = menu.findItem(R.id.action_sound_toggle);
            if (soundItem != null) {
                if (isSoundEnabled) {
                    soundItem.setIcon(R.drawable.ic_volume_on);
                    soundItem.setTitle("Disable Sound");
                } else {
                    soundItem.setIcon(R.drawable.ic_volume_off);
                    soundItem.setTitle("Enable Sound");
                }
            }
        }
    }

    private void setupSettingsDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        gameDurationSlider = findViewById(R.id.game_duration_slider);
        gameDurationText = findViewById(R.id.game_duration_text);
        applySettingsButton = findViewById(R.id.apply_settings_button);

        // change shot clock duration
        RadioGroup shotClockRadioGroup = findViewById(R.id.shot_clock_radio_group);
        RadioButton shotClock24Button = findViewById(R.id.shot_clock_24);
        RadioButton shotClock30Button = findViewById(R.id.shot_clock_30);

        if (shotClockDuration == 24) {
            shotClock24Button.setChecked(true);
            fullShotClockValue = 24;
            reducedShotClockValue = 14;
        } else {
            shotClock30Button.setChecked(true);
            fullShotClockValue = 30;
            reducedShotClockValue = 20;
            updateShotClockButtonsText();
        }

        shotClockRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.shot_clock_24) {
                shotClockDuration = 24;
                fullShotClockValue = 24;
                reducedShotClockValue = 14;
            } else if (checkedId == R.id.shot_clock_30) {
                shotClockDuration = 30;
                fullShotClockValue = 30;
                reducedShotClockValue = 20;
            }
        });

        gameDurationSlider.setProgress(selectedGameDuration);
        gameDurationText.setText(selectedGameDuration + " minutes");


        gameDurationSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int duration = Math.max(1, progress);
                selectedGameDuration = duration;
                gameDurationText.setText(duration + " minutes");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Not needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Not needed
            }
        });

        applySettingsButton.setOnClickListener(v -> {
            long newDurationMillis = selectedGameDuration * 60 * 1000;

            if (!isGameTimerRunning) {
                gameTimeLeftMillis = newDurationMillis;
                updateGameTimeDisplay(gameTimeLeftMillis);

                shotClockLeftMillis = shotClockDuration * 1000;
                updateShotClockDisplay(shotClockLeftMillis);

                updateShotClockButtonsText();
                updateShotClockButtonsListeners();

                Toast.makeText(this, "Settings updated", Toast.LENGTH_SHORT).show();
                drawerLayout.closeDrawers();
            } else {
                new AlertDialog.Builder(this)
                        .setTitle("Reset Game?")
                        .setMessage("Changing settings will reset the current game. Continue?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            pauseTimers();
                            gameTimeLeftMillis = newDurationMillis;
                            shotClockLeftMillis = shotClockDuration * 1000;
                            updateGameTimeDisplay(gameTimeLeftMillis);
                            updateShotClockDisplay(shotClockLeftMillis);

                            updateShotClockButtonsText();
                            updateShotClockButtonsListeners();

                            Toast.makeText(this, "Game reset with new settings", Toast.LENGTH_SHORT).show();
                            drawerLayout.closeDrawers();
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
    }

    private void updateShotClockButtonsText() {
        if (resetShotClock24Btn != null) {
            resetShotClock24Btn.setText("RESET " + fullShotClockValue);
        }
        if (resetShotClock14Btn != null) {
            resetShotClock14Btn.setText("RESET " + reducedShotClockValue);
        }
    }

    private void updateShotClockButtonsListeners() {
        if (resetShotClock24Btn != null) {
            resetShotClock24Btn.setOnClickListener(v -> resetShotClock(fullShotClockValue));
        }

        if (resetShotClock14Btn != null) {
            resetShotClock14Btn.setOnClickListener(v -> resetShotClock(reducedShotClockValue));
        }
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_sound_toggle) {
            // Toggle sound state
            isSoundEnabled = !isSoundEnabled;

            // Update the menu icon
            invalidateOptionsMenu();

            // Show feedback to user
            String message = isSoundEnabled ? "Sound enabled" : "Sound muted";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

            return true;
        }
        else if (itemId == R.id.action_settings) {
            if (drawerLayout.isDrawerOpen(findViewById(R.id.settings_drawer))) {
                drawerLayout.closeDrawer(findViewById(R.id.settings_drawer));
            } else {
                drawerLayout.openDrawer(findViewById(R.id.settings_drawer));
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSoundPlayers() {
        try {
            // Release any existing resources
            releaseMediaPlayers();

            // Create new media players
            shotClockBuzzer = MediaPlayer.create(this, R.raw.shot_clock_buzzer);
            gameClockBuzzer = MediaPlayer.create(this, R.raw.game_clock_buzzer);

            // Add error listener for debugging
            MediaPlayer.OnErrorListener errorListener = new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("SOUND_ERROR", "MediaPlayer error: " + what + ", " + extra);
                    return false;
                }
            };

            if (shotClockBuzzer != null) {
                shotClockBuzzer.setOnErrorListener(errorListener);
            } else {
                Log.e("SOUND_ERROR", "Failed to create shot clock buzzer");
            }

            if (gameClockBuzzer != null) {
                gameClockBuzzer.setOnErrorListener(errorListener);
            } else {
                Log.e("SOUND_ERROR", "Failed to create game clock buzzer");
            }
        } catch (Exception e) {
            Log.e("SOUND_ERROR", "Error initializing sound players", e);
        }
    }

    private void findViews() {
        try {
            homeScoreText = findViewById(R.id.homeScore);
            awayScoreText = findViewById(R.id.awayScore);
            gameTimeText = findViewById(R.id.gameTime);
            shotClockText = findViewById(R.id.shotClock);

            isLandscapeMode = getResources().getConfiguration().orientation ==
                    android.content.res.Configuration.ORIENTATION_LANDSCAPE;

            if (!isLandscapeMode) {
                homeAdd1 = findViewById(R.id.homeAdd1);
                homeSubtract = findViewById(R.id.homeSubtract);
                awayAdd1 = findViewById(R.id.awayAdd1);
                awaySubtract = findViewById(R.id.awaySubtract);
                startTimeBtn = findViewById(R.id.startTime);
                pauseTimeBtn = findViewById(R.id.pauseTime);
            }

            resetShotClock24Btn = findViewById(R.id.resetShotClock24);
            resetShotClock14Btn = findViewById(R.id.resetShotClock14);
            resetGameBtn = findViewById(R.id.resetGame);

        } catch (Exception e) {
            Log.e("VIEW_ERROR", "Error finding views", e);
        }
    }

    private void setupDigitalClocks() {
        if (digitalFont != null) {
            homeScoreText.setTypeface(digitalFont);
            awayScoreText.setTypeface(digitalFont);
            gameTimeText.setTypeface(digitalFont);
            shotClockText.setTypeface(digitalFont);
        }

        gameTimeText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));
        shotClockText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));
    }

    private void setupInitialValues() {
        updateHomeScore(0);
        updateAwayScore(0);
        updateGameTimeDisplay(gameTimeLeftMillis);
        updateShotClockDisplay(shotClockLeftMillis);
        shotClockExpired = false;
        gameClockExpired = false;

        gameTimeText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));
        shotClockText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));

        checkAndUpdateShotClockVisibility();
    }

    private void setupClickListeners() {
        if (!isLandscapeMode) {
            // Home
            if (homeAdd1 != null) {
                homeAdd1.setOnClickListener(v -> updateHomeScore(homeScore + 1));
            }
            if (homeSubtract != null) {
                homeSubtract.setOnClickListener(v -> updateHomeScore(homeScore - 1));
            }

            // Away
            if (awayAdd1 != null) {
                awayAdd1.setOnClickListener(v -> updateAwayScore(awayScore + 1));
            }
            if (awaySubtract != null) {
                awaySubtract.setOnClickListener(v -> updateAwayScore(awayScore - 1));
            }

            if (startTimeBtn != null) {
                startTimeBtn.setOnClickListener(v -> {
                    if (shotClockExpired) {
                        shotClockLeftMillis = 24 * 1000;
                        updateShotClockDisplay(shotClockLeftMillis);
                        shotClockExpired = false;
                        shotClockText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));
                    }

                    if (gameClockExpired) {
                        return;
                    }

                    startTimers();
                });
            }

            if (pauseTimeBtn != null) {
                pauseTimeBtn.setOnClickListener(v -> pauseTimers());
            }
        }

        resetShotClock24Btn.setOnClickListener(v -> resetShotClock(fullShotClockValue));
        resetShotClock14Btn.setOnClickListener(v -> resetShotClock(reducedShotClockValue));
        resetGameBtn.setOnClickListener(v -> {
            resetGame();
            if (isLandscapeMode && drawerLayout != null) {
                drawerLayout.closeDrawers();
            }
        });
    }


    private void updateHomeScore(int newScore) {
        homeScore = Math.max(0, newScore);
        homeScoreText.setText(String.format("%02d", homeScore));
    }

    private void updateAwayScore(int newScore) {
        awayScore = Math.max(0, newScore);
        awayScoreText.setText(String.format("%02d", awayScore));
    }

    private void startTimers() {
        if (!isGameTimerRunning) {
            gameTimeText.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
            shotClockText.setTextColor(getResources().getColor(android.R.color.holo_red_dark));

            startGameTimer();
            startShotClock();
            isGameTimerRunning = true;
        }
    }

    private void pauseTimers() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }
        if (shotTimer != null) {
            shotTimer.cancel();
        }

        gameTimeText.setTextColor(getResources().getColor(android.R.color.white));
        shotClockText.setTextColor(getResources().getColor(android.R.color.holo_red_light));

        isGameTimerRunning = false;
        isShotClockRunning = false;
    }

    private void startGameTimer() {
        if (gameTimer != null) {
            gameTimer.cancel();
        }

        long interval = gameTimeLeftMillis <= 60 * 1000 ? DETAILED_INTERVAL : STANDARD_INTERVAL;

        gameTimer = new CountDownTimer(gameTimeLeftMillis, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                gameTimeLeftMillis = millisUntilFinished;
                updateGameTimeDisplay(millisUntilFinished);

                checkAndUpdateShotClockVisibility();

                // Play buzzer sound early becuz there is delay when playing the sound
                if (millisUntilFinished <= 700 && millisUntilFinished > 500) {
                    playGameClockBuzzer();
                }

                if (millisUntilFinished <= 60 * 1000 && interval == STANDARD_INTERVAL) {
                    cancel();
                    startGameTimer();
                }
            }

            @Override
            public void onFinish() {
                gameTimeLeftMillis = 0;
                updateGameTimeDisplay(0);
                isGameTimerRunning = false;
                gameClockExpired = true;

                gameTimeText.setTextColor(getResources().getColor(android.R.color.white));

                gameTimeText.setBackground(getResources().getDrawable(R.drawable.expired_clock_border, null));

                if (shotTimer != null) {
                    shotTimer.cancel();
                    isShotClockRunning = false;
                }
            }
        }.start();
    }

    private void startShotClock() {
        if (shotTimer != null) {
            shotTimer.cancel();
        }

        if (gameTimeLeftMillis <= shotClockLeftMillis) {
            shotClockText.setVisibility(View.INVISIBLE);
            return;
        } else {
            shotClockText.setVisibility(View.VISIBLE);
        }

        long interval = shotClockLeftMillis <= 5 * 1000 ? DETAILED_INTERVAL : STANDARD_INTERVAL;

        shotTimer = new CountDownTimer(shotClockLeftMillis, interval) {
            @Override
            public void onTick(long millisUntilFinished) {
                shotClockLeftMillis = millisUntilFinished;
                updateShotClockDisplay(millisUntilFinished);

                checkAndUpdateShotClockVisibility();

                if (millisUntilFinished <= 700 && millisUntilFinished > 500) {
                    playShotClockBuzzer();
                }

                if (millisUntilFinished <= 5 * 1000 && interval == STANDARD_INTERVAL) {
                    cancel();
                    startShotClock();
                }
            }

            @Override
            public void onFinish() {
                shotClockLeftMillis = 0;
                updateShotClockDisplay(0);
                isShotClockRunning = false;
                shotClockExpired = true;

                shotClockText.setTextColor(getResources().getColor(android.R.color.holo_red_light));

                shotClockText.setBackground(getResources().getDrawable(R.drawable.expired_clock_border, null));

                if (isGameTimerRunning) {
                    pauseTimers();
                }
            }
        }.start();

        isShotClockRunning = true;
    }

    private void resetShotClock(int seconds) {
        shotClockLeftMillis = seconds * 1000;
        updateShotClockDisplay(shotClockLeftMillis);
        shotClockExpired = false;

        shotClockText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));

        checkAndUpdateShotClockVisibility();

        if (isGameTimerRunning) {
            startShotClock();
        }
    }

    private void resetGame() {
        pauseTimers();

        updateHomeScore(0);
        updateAwayScore(0);

        // Reset timer
        gameTimeLeftMillis = selectedGameDuration * 60 * 1000;
        shotClockLeftMillis = shotClockDuration * 1000;
        updateGameTimeDisplay(gameTimeLeftMillis);
        updateShotClockDisplay(shotClockLeftMillis);

        shotClockExpired = false;
        gameClockExpired = false;

        gameTimeText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));
        shotClockText.setBackground(getResources().getDrawable(R.drawable.clock_border, null));

        checkAndUpdateShotClockVisibility();

        // Clear saved game state
        clearSavedGameState();
    }


    private void updateGameTimeDisplay(long timeMillis) {
        if (timeMillis < 60 * 1000) {
            double seconds = timeMillis / 1000.0;
            gameTimeText.setText(String.format("%.1f", seconds));
        } else {
            int minutes = (int) (timeMillis / 1000) / 60;
            int seconds = (int) (timeMillis / 1000) % 60;
            gameTimeText.setText(String.format("%02d:%02d", minutes, seconds));
        }
    }

    private void updateShotClockDisplay(long timeMillis) {
        if (timeMillis <= 5 * 1000) {
            double seconds = timeMillis / 1000.0;
            shotClockText.setText(String.format("%.1f", seconds));
        } else {
            int seconds = (int) (timeMillis / 1000);
            shotClockText.setText(String.format("%02d", seconds));
        }
    }

    private void checkAndUpdateShotClockVisibility() {
        // If game clock have less time than shot clock, hide shot clock
        if (gameTimeLeftMillis <= shotClockLeftMillis) {
            shotClockText.setVisibility(View.INVISIBLE);
        } else {
            shotClockText.setVisibility(View.VISIBLE);
        }
    }

    private void playShotClockBuzzer() {
        if (!isSoundEnabled) {
            return;
        }

        try {
            if (shotClockBuzzer != null) {
                if (shotClockBuzzer.isPlaying()) {
                    shotClockBuzzer.stop();
                    shotClockBuzzer.reset();
                    shotClockBuzzer = MediaPlayer.create(this, R.raw.shot_clock_buzzer);
                }

                Log.d("SOUND_INFO", "Starting shot clock buzzer");
                shotClockBuzzer.start();
            } else {
                Log.e("SOUND_ERROR", "Shot clock buzzer is null");
                initSoundPlayers();

                if (shotClockBuzzer != null && isSoundEnabled) {
                    shotClockBuzzer.start();
                }
            }
        } catch (Exception e) {
            Log.e("SOUND_ERROR", "Error playing shot clock buzzer", e);
        }
    }

    private void playGameClockBuzzer() {
        if (!isSoundEnabled) {
            return;
        }

        try {
            if (gameClockBuzzer != null) {
                if (gameClockBuzzer.isPlaying()) {
                    gameClockBuzzer.stop();
                    gameClockBuzzer.reset();
                    gameClockBuzzer = MediaPlayer.create(this, R.raw.game_clock_buzzer);
                }

                Log.d("SOUND_INFO", "Starting game clock buzzer");
                gameClockBuzzer.start();
            } else {
                Log.e("SOUND_ERROR", "Game clock buzzer is null");
                initSoundPlayers();

                if (gameClockBuzzer != null && isSoundEnabled) {
                    gameClockBuzzer.start();
                }
            }
        } catch (Exception e) {
            Log.e("SOUND_ERROR", "Error playing game clock buzzer", e);
        }
    }

    private void releaseMediaPlayers() {
        if (shotClockBuzzer != null) {
            try {
                if (shotClockBuzzer.isPlaying()) {
                    shotClockBuzzer.stop();
                }
                shotClockBuzzer.release();
            } catch (Exception e) {
                Log.e("SOUND_ERROR", "Error releasing shot clock buzzer", e);
            }
            shotClockBuzzer = null;
        }

        if (gameClockBuzzer != null) {
            try {
                if (gameClockBuzzer.isPlaying()) {
                    gameClockBuzzer.stop();
                }
                gameClockBuzzer.release();
            } catch (Exception e) {
                Log.e("SOUND_ERROR", "Error releasing game clock buzzer", e);
            }
            gameClockBuzzer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pauseTimers();
        saveGameState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseMediaPlayers();
    }
}
