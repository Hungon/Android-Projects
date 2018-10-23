package com.trials.harmony;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;

import org.jetbrains.annotations.Contract;

/**
 * Created by Kohei Moroi on 7/7/2016.
 */
public class ScoreManager implements HasScene, HasScoreImage,
        HasRecords, HasRecognitionId, HasButtons,
        HasSystem, FileManager {
    // filed
    private static int      mEachScorePoint[];
    private Context         mContext;
    private Image           mImage;
    private int mCurrentScene;
    private CharacterEx mScoreImages[];
    private Score mScoreNumber[];           // to display number image
    private ScoreColour mScoreColour[];     // to display colour image
    private Time mTime;
    private int mPreviewCountCalledRecognizer;
    private int mCurrentMode;
    private int mColourIdMax[];
    private static int mAggregatePoints;

    // constructor
    public ScoreManager(Context context, Image image) {
        this.mImage = image;
        this.mContext = context;
        // to get the current scene
        this.mCurrentScene = SceneManager.GetCurrentScene();
        // to get the current mode
        this.mCurrentMode = SystemManager.GetPlayMode();
        // to diverge the initialization from the current scene.
        if (this.mCurrentScene == SCENE_RESULT) {
            // allot the memory
            // for each point to get
            mEachScorePoint = new int[SCORE_KIND];
            for (int i = 0; i < mEachScorePoint.length; i++) {
                mEachScorePoint[i] = 0;
            }
            // score images and score number
            this.mScoreImages = new CharacterEx[SCORE_KIND];
            this.mScoreNumber = new Score[SCORE_KIND];
            for (int i = 0; i < this.mScoreImages.length; i++) {
                this.mScoreImages[i] = new CharacterEx(context, image);
                this.mScoreNumber[i] = new Score(context, image);
            }
            // to get pile of id that caught correct id which are except for empty.
            int idMax[] = RecognizerManager.GetStoredId();
            // reset id
            RecognizerManager.ResetStoredId();
            this.mColourIdMax = new int[idMax.length];
            System.arraycopy(idMax,0,this.mColourIdMax,0,idMax.length);
            // to allot the memory to colour images
            this.mScoreColour = new ScoreColour[idMax.length];
            // to set these colour type
            for (int i = 0; i < this.mScoreColour.length; i++) {
                this.mScoreColour[i] = new ScoreColour(context,image);
            }
        }
    }
    public ScoreManager(Context context, Image image, int idMax) {
        this.mImage = image;
        this.mContext = context;
        // to get the current scene
        this.mCurrentScene = SceneManager.GetCurrentScene();
        // to get the current mode
        this.mCurrentMode = SystemManager.GetPlayMode();
        // initialize image of colour
        if (this.mCurrentScene == SCENE_PLAY) {
            this.mScoreColour = new ScoreColour[idMax];
            for (int i = 0; i < this.mScoreColour.length; i++) {
                this.mScoreColour[i] = new ScoreColour(context,image);
            }
            // time
            this.mTime = new Time(context,image);
            // score
            this.mScoreNumber = new Score[1];
            this.mScoreNumber[0] = new Score(this.mContext,this.mImage);
        }
    }
    /*
        Initialize
    */
    public void InitScore() {
        // to allot the memory for record
        Record record = new Record(this.mContext);
        // To diverge the process from the current scene.
        // To set each score value from RecognizerManager or local file.
        // In Result scene,
        if (this.mCurrentScene == SCENE_RESULT) {
            // To initialize each score
            // to get each score from RecognizerManager
            int count[] = {
                    RecognizerManager.GetAggregateCount(),
                    RecognizerManager.GetMaxChainCorrectCount()
            };
            // each score
            int t = RecognizerManager.GetAggregateCount() * 100 + RecognizerManager.GetMaxChainCorrectCount() * 50;
            int score[] = {count[0], count[1], t};
            System.arraycopy(score, 0, mEachScorePoint, 0, score.length);
            // to update the best record
            // which file relates to music id.
            // hence to add the music id to the suffix.
            int musicId = MusicSelector.GetCurrentElement();
            int level = SystemManager.GetGameLevel();
            record.UpdateBestRecord(
                    FILE_EACH_BEST_RECORD[this.mCurrentMode]+
                            FILE_NAME_EACH_SUFFIX[level]+
                            Integer.toString(musicId),
                    score);
            // Set score images
            this.SetScoreImages();

            // to initialize the bezier line to colour images
            // max value
            int rowMax = 13;
            int rowCounter = 0;
            int lineCounter = 0;
            // these are must be the id which is not empty.
            int colourType[] = RecognizerManager.ToConvertTheRecognitionIdIntoElement(
                    HasRecognitionWords.GUIDANCE_MESSAGE_COLOUR,this.mColourIdMax);
            for (int i = 0; i < this.mScoreColour.length; i++) {
                this.mScoreColour[i].InitScore(
                        "scorecolours",
                        new Point(400,-100),
                        SCORE_COLOUR_SIZE,
                        100, 0.2f, -1);
                // the count that arrange the image to row
                // Bezier coordination
                Point bezier[] = {
                        new Point(300,-100),
                        new Point(200,0),
                        new Point(100,25),
                        new Point(50,35),
                        new Point(10,150)
                };
                // to set bezier setting
                for (Point b: bezier) {
                    b.x += SCORE_COLOUR_SIZE.x*rowCounter;
                    b.y += SCORE_COLOUR_SIZE.y*lineCounter;
                }
                bezier[0].y -= 50*lineCounter;
                // when the current row counter reached to max value,
                // to decline the new line.
                if (rowMax <= rowCounter) {
                    rowCounter = 0;
                    lineCounter++;
                } else {
                    rowCounter++;
                }
                // set the bezier coordination that default values
                this.mScoreColour[i].SetFixedBezier(bezier);
                // set the bezier coordination that changeable values
                this.mScoreColour[i].SetBezier(bezier);
                // set add alpha and fixed time
                this.mScoreColour[i].SetVariableAlphaAndFixedInterval(4,2);
                // set type to create
                this.mScoreColour[i].SetTypeToCreate(colourType[i]);
            }
        } else if (this.mCurrentScene == SCENE_PLAY) {
            for (int i = 0; i < this.mScoreColour.length; i++) {
                // to set each default value
                this.mScoreColour[i].InitScore(
                        "scorecolours",
                        new Point(400,-100),
                        SCORE_COLOUR_SIZE,
                        100, 0.2f, -1);
                // Bezier coordination
                Point bezier[] = {
                        new Point(300,-50),
                        new Point(200,0),
                        new Point(100,25),
                        new Point(50,35),
                        new Point(25,50)
                };
                // to set bezier setting
                for (Point b: bezier) b.x += SCORE_COLOUR_SIZE.x*i;
                bezier[0].y += -50*i;
                bezier[2].y += 20*i;
                // set the bezier coordination that default values
                this.mScoreColour[i].SetFixedBezier(bezier);
                // set the bezier coordination that changeable values
                this.mScoreColour[i].SetBezier(bezier);
                // set add alpha and fixed time
                this.mScoreColour[i].SetVariableAlphaAndFixedInterval(4,2);
            }
            // set timer
            this.mTime.InitTime(0,100,SCORE_NUMBER_COLOR_WHITE,Score.SCORE_DIRECTION_ALPHA);
            // set score number
            this.mScoreNumber[0].InitScore(
                    FILE_SCORE_NUMBER[SCORE_TYPE_GRADATION],
                    new Point(120,50),SCORE_NUMBER_SIZE,Score.SCORE_DIRECTION_GRADUALLY,0,5);

            mAggregatePoints = 0;
        }
        // the preview count that called the recognizer
        this.mPreviewCountCalledRecognizer = 0;
    }
    /*
        Set score images
    */
    private void SetScoreImages() {
        String imageFile = "scoreimages";
        // To initialize each score image
        // starting position
        Point pos = new Point(20, 290);
        Point space = new Point(10, 60);
        Point size[] = {
                new Point(360, 48),      // aggregate
                new Point(250, 48),      // chain
                new Point(250, 48),      // total
        };
        // to set score image and score number
        for (int i = 0; i < this.mScoreImages.length; i++) {
            this.mScoreImages[i].InitCharacterEx(
                    imageFile,
                    pos.x, pos.y + (size[0].y + space.y) * i,
                    size[i].x, size[i].y,
                    0, size[0].y * i, 0, 1.0f, i);
            int posY = pos.y + (size[0].y + space.y) * i;
            this.mScoreNumber[i].InitScore(
                    FILE_SCORE_NUMBER[SCORE_TYPE_NORMAL],
                    new Point(pos.x, posY + size[0].y),
                    SCORE_NUMBER_SIZE,
                    Score.SCORE_DIRECTION_ALPHA,
                    mEachScorePoint[i], 5);
            // to set the parameter for direction of alpha
            this.mScoreNumber[i].SetAlphaAndFixedInterval(2, 2);
        }
    }
    /*
        Update
    */
    public void UpdateScore() {
        if (this.mCurrentScene == SCENE_RESULT) {
            for (int i = 0; i < SCORE_KIND; i++) {
                this.mScoreNumber[i].UpdateScore();
                this.mScoreImages[i].VariableAlpha(4,2);
            }
            for (int i = 0; i < this.mScoreColour.length; i++) {
                if (!this.mScoreColour[i].GetExistence()) {
                    int type = this.mScoreColour[i].GetTypeToCreate();
                    if (SCORE_COLOUR_LIST[0] <= type && type <= SCORE_COLOUR_LIST[SCORE_COLOUR_LIST.length-1]) {
                        if (this.mScoreColour[i].mInterval.ToMakeTheInterval(5 * i)) {
                            this.mScoreColour[i].CreateObject(type);
                        }
                    }
                } else {
                    // when the colour is existing, to update the each function which are
                    // bezier line, gradually scale up and to be transparent
                    this.mScoreColour[i].UpdateScore(false);
                }
            }
            // In play scene,
        } else if (this.mCurrentScene == SCENE_PLAY) {

            // update timer
            this.mTime.UpdateTime();
            // When to return variables of the correct answer that was recognized by Recognizer manager,
            // to create colour image due to notice.
            this.IsCheckingToCreateImages();
            // update score number
            this.mScoreNumber[0].UpdateScore();

            for (int i = 0; i < this.mScoreColour.length; i++) {
                if (!this.mScoreColour[i].GetExistence()) {
                    int type = this.mScoreColour[i].GetTypeToCreate();
                    if (SCORE_COLOUR_LIST[0] <= type && type <= SCORE_COLOUR_LIST[SCORE_COLOUR_LIST.length-1]) {
                        if (this.mScoreColour[i].mInterval.ToMakeTheInterval(5 * i)) {
                            this.mScoreColour[i].CreateObject(type);
                        }
                    }
                } else {
                    // When colour's current position reached to the terminate position,
                    // to return false.
                    if (!this.mScoreColour[i].UpdateScore(true)) {
                        // get center position of character
                        Point charaPos = RecognitionCharacter.GetPosition();
                        PointF charaSize = RecognitionCharacter.GetWholeSize();
                        Point charaCenter = new Point(
                                charaPos.x + ((int) charaSize.x >> 1),
                                charaPos.y + ((int) charaSize.y >> 1));
                        // get center position of the colour
                        Point colourPos = this.mScoreColour[i].GetCenterPosition();
                        // the difference distance between the colour and the character.
                        Point differencePos = new Point(
                                charaCenter.x-colourPos.x,
                                charaCenter.y-colourPos.y);
                        // When score's scale reached to max value,
                        // to move character's position by bezier after fixed interval.
                        if (this.mScoreColour[i].GetScale() == this.mScoreColour[i].GetMaxScale() &&
                                this.mScoreColour[i].GetBezierIsDefault()) {
                            if (this.mScoreColour[i].mInterval.ToMakeTheInterval(100)) {
                                int max = 5;
                                // Bezier coordination
                                Point bezier[] = new Point[max];
                                for (int j = 0; j < bezier.length; j++) bezier[j] = new Point();
                                // add value
                                Point addPos = new Point(
                                        differencePos.x / max,
                                        differencePos.y / max);
                                for (int j = 0; j < bezier.length; j++) {
                                    if (j == max-1) {
                                        // eventually, to substitute the center position of character to the last of bezier position.
                                        bezier[j].x = charaCenter.x;
                                        bezier[j].y = charaCenter.y;
                                        break;
                                    }
                                    bezier[j].x = colourPos.x + (addPos.x * j);
                                    bezier[j].y = colourPos.y + (addPos.y * (j-1));
                                }
                                // set bezier setting
                                this.mScoreColour[i].SetBezier(bezier);
                                // was changed the bezier value
                                this.mScoreColour[i].WasChangedBezier();
                            }
                        }
                        if (Math.abs(differencePos.y) < 20) {
                            this.mScoreColour[i].SetPosition(charaCenter.x,charaCenter.y);
                        }
                    }
                }
            }
        }
    }
    /*
        Draw
    */
    public void DrawScore() {
        // In result and record view,
        if (this.mCurrentScene == SCENE_RESULT) {
            this.mImage.fillRect(0,0,480,800,Color.CYAN);
            for (int i = 0; i < this.mScoreImages.length; i++) {
                this.mScoreImages[i].DrawCharacterEx();
                // score number
                this.mScoreNumber[i].DrawScore(SCORE_NUMBER_COLOR_BLUE);
            }
            for (ScoreColour c: this.mScoreColour) {
                c.DrawScore();
            }
            // In play,
        } else if (this.mCurrentScene == SCENE_PLAY) {
            this.mTime.DrawTime();
            this.mScoreNumber[0].DrawScore(SCORE_NUMBER_COLOR_YELLOW);
            for (ScoreColour colour: this.mScoreColour) {
                colour.DrawScore();
            }
        }
    }
    /*
        Release
    */
    public void ReleaseScore() {
        // score image and number
        if (this.mScoreImages != null) {
            for (int i = 0; i < SCORE_KIND; i++) {
                this.mScoreImages[i].ReleaseCharacterEx();
                this.mScoreImages[i] = null;
            }
        }
        if (this.mScoreNumber != null) {
            for (int i = 0; i < this.mScoreNumber.length; i++) {
                this.mScoreNumber[i].ReleaseScore();
                this.mScoreNumber[i] = null;
            }
        }
        if (mEachScorePoint != null) {
            mEachScorePoint = null;
        }
        if (this.mScoreColour != null) {
            for (int i=0;i<this.mScoreColour.length;i++) {
                this.mScoreColour[i].ReleaseScore();
                this.mScoreColour[i] = null;
            }
        }
        if (this.mTime != null) {
            this.mTime.ReleaseTime();
            this.mTime = null;
        }
        if (this.mColourIdMax != null) this.mColourIdMax = null;
    }
    /*
        To check to create
    */
    private void IsCheckingToCreateImages() {
        int count = RecognitionMode.GetCurrentCountCalledRecognizer();
        if (this.mPreviewCountCalledRecognizer < count) {

            // update aggregate points and add aggregate points to score
            mAggregatePoints = RecognizerManager.GetAggregateCount() * 100 + RecognizerManager.GetMaxChainCorrectCount() * 50;
            this.mScoreNumber[0].setTerminateNumber(mAggregatePoints);

            int button[] = null;
            // to diverge the substitution from the current mode.
            if (this.mCurrentMode == MODE_SOUND) {
                // get the button type
                int length = ColourManager.getCurrentButtonType().length;
                button = new int[length];
                System.arraycopy(ColourManager.getCurrentButtonType(), 0, button, 0, length);
            } else if (this.mCurrentMode == MODE_SENTENCE) {
                // get the button type
                int length = SentenceManager.getCurrentButtonType().length;
                button = new int[length];
                System.arraycopy(SentenceManager.getCurrentButtonType(), 0, button, 0, length);
            } else if (this.mCurrentMode == MODE_ASSOCIATION_IN_EMOTIONS ||
                    this.mCurrentMode == MODE_ASSOCIATION_IN_FRUITS ||
                    this.mCurrentMode == MODE_ASSOCIATION_IN_ALL) {
                // get the button type
                int length = AssociationManager.getCurrentButtonType().length;
                button = new int[length];
                System.arraycopy(AssociationManager.getCurrentButtonType(), 0, button, 0, length);
            }
            try {
                if (button != null) {
                    // get recognized sentence
                    int id[] = RecognizerManager.GetRecognitionId();
                    for (int i = 0; i < button.length; i++) {
                        int element;
                        // to diverge the substitution from the current mode.
                        element = PlayManager.convertTypeIntoElement(this.mCurrentMode,button[i]);
                        // set type to create image
                        this.mScoreColour[i].SetTypeToCreate(element);
                        // validate correct answer by the button type and recognized id
                        // if answer is correct, to appear clearly colour image
                        if (id[i] != RECOGNITION_ID_EMPTY) {
                            this.mScoreColour[i].SetMaxScale(1.0f);
                        } else {
                            this.mScoreColour[i].SetMaxScale(0.3f);
                        }
                    }
                    // to update preview count
                    this.mPreviewCountCalledRecognizer = count;
                }
            } catch (NullPointerException e) {
                MainView.Toast(e.getMessage());
            }
        }
    }
    /******************************************************
     Each setter functions
     *****************************************************/
    /******************************************************
     Each getter functions
     *****************************************************/
    /*
        Get each score
    */
    @Contract(pure = true)
    public static int[] GetEachScore() {
        int res[] = {-1};
        return (mEachScorePoint == null) ? res : mEachScorePoint;
    }
    static int getAggregatePoints() { return mAggregatePoints; }
}
