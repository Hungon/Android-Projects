package com.trials.supertriathlon;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;

/**
 * Created by USER on 2/13/2016.
 */
public class BriefStage extends Scene {

    // static variables
    // each explanations for the stage
    private final static String     EXPLAIN_OFF_ROAD =
            "　キャラクターはタップした位置へ\\n\n" +
            "移動します。\\e\n" +
            "キャラクターは前方から迫る障害物を\\n\n" +
            "回避しつつ前へ進んでいきます。\\e\n" +
            "キャラクターがこのジャンプ台に触れると\\n\n" +
            "ジャンプ行動を行います。\\e\n" +
            "その時、この円状のコマンドが現れます。\\e\n" +
            "透けている円を透けていない円の大きさくらいで、\\n\n" +
            "タップすると入力成功です。\\e\n" +
            "\\m\\y準備はいいですか？\\e";
    private final static String     EXPLAIN_ROAD =
            "　キャラクターは、タップした位置へ\\n\n" +
            "移動します。\\e\n" +
            "キャラクターをタップすると、\\n\n" +
            "障害物を回避するジャンプ行動を\\n\n" +
            "行います。\\e\n" +
            "更に、競争相手をタップすることで、\\n\n" +
            "攻撃行動をとることができます。\\e\n" +
            "もちろん、向こうから攻撃されることも\\n\n" +
            "あるので、気をつけてください。\\e\n" +
            "ただの障害物です。\\n\n" +
            "ジャンプか移動で回避できます。\\e\n" +
            "特殊なハードルです。\\n\n" +
            "黒色を除き、走者がジャンプで飛び越えることで、\\n\n" +
            "有利な効果を得ることができます。\\e\n" +
            "走者が効果を得ている間は、これら画像が\\n\n" +
            "現れます。\\e\n" +
            "キャラクターの体力が尽きると、\\n\n" +
            "ゲームオーバーに移ります。\\e\n" +
            "\\m\\y準備はいいですか？\\e";
    private final static String     EXPLAIN_SEA =
            "　右下のコントローラーを使って、\\n\n" +
            "キャラクターを移動させます。\\e\n" +
            "コントローラーを使う前に、\\n\n" +
            "画面に触れていると移動することはできません。\\e\n" +
            "この画像をタップすると、\\n\n" +
            "攻撃を行うことができます。\\e\n" +
            "更に、この画像をタップすることで、\\n\n" +
            "特殊な攻撃を行えます。\\e\n" +
            "攻撃ゲージは敵を倒すか、\\n\n" +
            "攻撃されることで溜まります。\\e\n" +
            "敵を倒すとアイテムが前方に出現します。\\n\n" +
            "アイテムはすぐに効果がでるものと、\\n\n" +
            "そうでないものがあります。\\e\n" +
            "アイテムを得たら、この画像をタップして\\n\n" +
            "使用します。\\e\n" +
            "この光をくぐると、ボーナスが得られます。\\e\n" +
            "\\m\\y準備はいいですか？\\e";

    // Object position-Y
    private final static int        OBJECT_POSITION_Y = 300;
    // OK button
    public final static Point       OK_SIZE     = new Point(80,80);
    // Explanation words setting
    private final static int        EXPLANATION_FONT_SIZE = 20;
    // Interval time
    private final static int        INTERVAL_TIME = 100;

    // filed
    private Image           mImage;                              // image object
    private Context         mContext;                           // activity
    private Sound           mSound[] = new Sound[2];             // sound object to play SE or background music
    private String          mBGMfileName;
    private BaseCharacter   mChara[];
    private Animation       mAni[];
    private BaseCharacter   mOK;
    private BaseCharacter   mAnswer[] = new BaseCharacter[2];
    private Menu            mMenu;
    private int             mBriefTheStage;
    private int             mIntervalTime;
    private int             mBriefIndex;
    private int             mBriefIndexNext;
    private int             mBriefIndexMax;
    private int             mResponse;
    private Talk            mTalk;
    private RoadPlayer      mRoadPlayer;
    private RoadCompetitorManager mCompetitorManager;
    private SeaPlayer       mSwimmer;
    private CharacterController mController;
    private CharacterEffect mEffect[];
    private Point           mExplanationPos;
    private Point           mControllerPos;

    /*
        Constructor
    */
    public BriefStage(Context context, Image image) {
        // get image object
        this.mImage = image;
        // get activity
        this.mContext = context;
        // set sound
        for (int i = 0; i < this.mSound.length; i++) {
            this.mSound[i] = new Sound(this.mContext);
        }
        // Menu
        this.mMenu = new Menu(context, image);
        // for accept button
        this.mOK = new BaseCharacter(image);
        // for response button
        for (int i = 0; i < this.mAnswer.length; i++) {
            this.mAnswer[i] = new BaseCharacter(image);
        }
        // Talk class
        this.mTalk = new Talk(context, image, EXPLANATION_FONT_SIZE, Color.WHITE);
        // explanation position
        this.mExplanationPos = new Point();
    }

    /*
        Initialize
    */
    public int Init() {
        
        // Get stage number to next
        this.mBriefTheStage = Play.GetNextStageNumber();

        // using BGM
        this.mBGMfileName = "selectmode";
        // using SE
        String seFiles[] = {
                "click",
                "cancel",
        };
        // create SE
        for(int i = 0; i < seFiles.length; i++) this.mSound[i].CreateSound(seFiles[i]);
        // using image file
        String imageFile[] = {"accept", "menu"};
        // load
        this.mOK.LoadCharaImage(this.mContext,imageFile[0]);
        for (BaseCharacter ch: this.mAnswer) ch.LoadCharaImage(this.mContext, imageFile[1]);

        // Image setting
        // OK button
        // get screen size
        Point screen = GameView.GetScreenSize();
        Point pos = new Point();
        pos.x = screen.x-(OK_SIZE.x+10);
        if (screen.x == 480) {
            pos.y = screen.y-(OK_SIZE.y+20);
        } else if (screen.x == 800) {
            pos.y = 10;
        }
        this.mOK.mSize.x = OK_SIZE.x;
        this.mOK.mSize.y = OK_SIZE.y;
        this.mOK.mPos.x = pos.x;
        this.mOK.mPos.y = pos.y;
        this.mOK.mExistFlag = false;
        
        // Yes and No button
        // Answer
        // common
        for (int i = 0; i < 2; i++) {
            this.mAnswer[i].mSize.x = Option.ANSWER_SIZE.x;
            this.mAnswer[i].mSize.y = Option.ANSWER_SIZE.y;
            this.mAnswer[i].mOriginPos.y = Menu.MENU_BUTTON_SIZE.y + this.mAnswer[i].mSize.y*i;
            this.mAnswer[i].mPos.y = screen.y>>1;
        }
        // yes position
        this.mAnswer[0].mPos.x = (screen.x>>1) - (this.mAnswer[0].mSize.x + 30);
        // no position
        this.mAnswer[1].mPos.x = (screen.x>>1) + 30;


        // Explanation time
        this.mIntervalTime = 0;
        // Brief process
        this.mBriefIndexNext = 1;
        this.mBriefIndex = 0;
        // the material that yes or no
        this.mResponse = -1;

        // Initialize Menu
        this.mMenu.InitMenuBack(Menu.BACK_SCENE_SELECT);
        
        // Initialize the stage from divergence
        switch(this.mBriefTheStage) {
            case Play.STAGE_OFF_ROAD:
                this.InitOffroad();
                break;
            case Play.STAGE_ROAD:
                this.InitRoad();
                break;
            case Play.STAGE_SEA:
                this.InitSea();
                break;
        }

        // Create Talk
        // file path
        String talkFile[] = {EXPLAIN_OFF_ROAD, EXPLAIN_ROAD, EXPLAIN_SEA};
        // starting position to explain
        this.mExplanationPos.y = 100;
        if (screen.x == 480) {  // when portrait
            this.mExplanationPos.x = 10;
        } else if (screen.x == 800) { // landscape
            this.mExplanationPos.x = 200;
        }
        try {
            this.mTalk.CreateTalkEx(talkFile[this.mBriefTheStage], this.mExplanationPos);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // if ( false )		return	CScene.SCENE_ERROR;
        return Scene.SCENE_MAIN;
    }

    /********************************************************************************
        Each initialization
    ******************************************************************************/
    /*
        Initialize the brief stage for off-road stage.
    */
    private void InitOffroad() {
        // Allot memory
        // Base character
        this.mChara = new BaseCharacter[5];
        for (int i = 0; i < this.mChara.length; i++) {
            this.mChara[i] = new BaseCharacter(this.mImage);
        }
        // Animation
        this.mAni = new Animation[2];
        for (int i = 0; i < this.mAni.length; i++) {
            this.mAni[i] = new Animation();
        }
        // using images
        String imageFiles[] = {
                "offroadplayer",        // player
                "offroadstone",         // obstacle
                "offroadjump",          // jump point
                "accommand",            // command image
                "accommand",            // input image
        };
        // load image file.
        for (int i = 0; i < this.mChara.length; i++) {
            this.mChara[i].LoadCharaImage(this.mContext, imageFiles[i]);
        }
        
        // index max that explain the brief stage
        this.mBriefIndexMax = 5;

        // Each image setting
        // get screen size
        Point screen = GameView.GetScreenSize();
        // Player
        this.mChara[0].mSize.x = OffroadPlayer.PLAYER_SIZE.x;
        this.mChara[0].mSize.y = OffroadPlayer.PLAYER_SIZE.y;
        this.mChara[0].mPos.x = (screen.x-this.mChara[0].mSize.x)>>1;
        this.mChara[0].mPos.y = 600;
        this.mChara[0].mSpeed = OffroadPlayer.PLAYER_DEFAULT_SPEED;
        this.mChara[0].mExistFlag = true;
        // animation setting
        // the type that show action
        this.mAni[0].mType = OffroadPlayer.ANIMATION_TYPE_NORMAL;
        this.mAni[0].SetAnimation(
                this.mChara[0].mOriginPos.x, this.mChara[0].mOriginPos.y,
                this.mChara[0].mSize.x, this.mChara[0].mSize.y,
                OffroadPlayer.PLAYER_ANIMATION_TYPE_NORMAL_CNT_MAX,
                OffroadPlayer.PLAYER_ANIMATION_TYPE_NORMAL_FRAME,
                OffroadPlayer.ANIMATION_TYPE_NORMAL);
        
        // Each Object position-Y
        for (int i = 1; i < this.mChara.length; i++) this.mChara[i].mPos.y = OBJECT_POSITION_Y;
        // Obstacles
        // Rock
        this.mChara[1].mSize.x = OffroadObstacles.OBSTACLE_ROCK_SIZE.x;
        this.mChara[1].mSize.y = OffroadObstacles.OBSTACLE_ROCK_SIZE.y;
        this.mChara[1].mPos.x = (screen.x-this.mChara[1].mSize.x)/2;
        // animation
        this.mAni[1].SetAnimation(
                0, 0,
                this.mChara[1].mSize.x,
                this.mChara[1].mSize.y,
                OffroadObstacles.OBSTACLE_ROCK_ANIMATION_COUNT_MAX,
                OffroadObstacles.OBSTACLE_ROCK_ANIMATION_FRAME,
                OffroadObstacles.OBSTACLE_ROCK
        );
        // Jump point
        this.mChara[2].mSize.x = OffroadObstacles.OBSTACLE_JUMP_POINT_SIZE.x;
        this.mChara[2].mSize.y = OffroadObstacles.OBSTACLE_JUMP_POINT_SIZE.y;
        this.mChara[2].mPos.x = (screen.x-this.mChara[2].mSize.x)/2;

        // Command
        for (int i = 3; i <= 4; i++) {
            this.mChara[i].mSize.x = ActionCommand.COMMAND_SIZE.x;
            this.mChara[i].mSize.y = ActionCommand.COMMAND_SIZE.y;
        }
        // position
        // command
        this.mChara[3].mPos.x = ActionCommand.COMMAND_STARTING_POSITION.x;
        this.mChara[3].mPos.y = ActionCommand.COMMAND_STARTING_POSITION.y;
        // input
        this.mChara[4].mPos.x = ActionCommand.COMMAND_STARTING_POSITION.x;
        this.mChara[4].mPos.y = ActionCommand.COMMAND_STARTING_POSITION.y;
        this.mChara[4].mScale = ActionCommand.INPUT_STARTING_SCALE;
        this.mChara[4].mAlpha = ActionCommand.INPUT_DEFAULT_ALPHA;
    }
    /*
        Initialize the road-stage the brief.
    */
    private void InitRoad() {
        // effect
        this.mEffect = new CharacterEffect[1];
        for (int i = 0; i < this.mEffect.length; i++) this.mEffect[i] = new CharacterEffect(this.mContext, this.mImage);
        // set the effect
        this.mEffect[0].InitCharacterEffect(CharacterEffect.EFFECT_FINGER_TAP);

        // using image files
        String imageFiles[] = {
                "roadeffectballs",
                "roadhurdle",
                "roadcone"
        };
        // player and competitor
        this.mRoadPlayer = new RoadPlayer(this.mContext, this.mImage);
        // competitor
        this.mCompetitorManager = new RoadCompetitorManager(this.mContext, this.mImage);

        // index max that explain the brief stage
        this.mBriefIndexMax = 8;

        // each initializations
        this.mRoadPlayer.InitToExplain();
        this.mCompetitorManager.InitToExplain();

        // except for player and competitor, to allot memory
        this.mChara = new BaseCharacter[imageFiles.length];
        for (int i = 0; i < this.mChara.length; i++) {
            this.mChara[i] = new BaseCharacter(this.mImage);
            this.mChara[i].LoadCharaImage(this.mContext, imageFiles[i]);
        }
        // Each image setting
        // effect ball
        this.mChara[0].mSize.x = CharacterEffect.BALL_SIZE.x;
        this.mChara[0].mSize.y = CharacterEffect.BALL_SIZE.y;
        this.mChara[0].mPos.x = RoadPlayer.GetPosition().x + this.mChara[0].mSize.x;
        this.mChara[0].mPos.y = RoadPlayer.GetPosition().y;
        // Hurdle
        this.mChara[1].mSize.x = RoadObstacles.HURDLE_SIZE.x;
        this.mChara[1].mSize.y = RoadObstacles.HURDLE_SIZE.y;
        this.mChara[1].mPos.x = 66;       // starting position-X
        this.mChara[1].mPos.y = 200;
        // Cone
        // get screen size
        Point screen = GameView.GetScreenSize();
        this.mChara[2].mSize.x = RoadObstacles.CONE_SIZE.x;
        this.mChara[2].mSize.y = RoadObstacles.CONE_SIZE.y;
        this.mChara[2].mPos.x = (screen.x-this.mChara[2].mSize.x)/2;
        this.mChara[2].mPos.y = 200;
    }
    /*
        Initialize the brief stage for sea
    */
    private void InitSea() {
        // index max that explain the brief stage
        this.mBriefIndexMax = 8;

        Point screen = GameView.GetScreenSize();
        // allot memory
        // effect
        this.mEffect = new CharacterEffect[1];
        for (int i = 0; i < this.mEffect.length; i++) this.mEffect[i] = new CharacterEffect(this.mContext, this.mImage);
        // set the effect
        this.mEffect[0].InitCharacterEffect(CharacterEffect.EFFECT_FINGER_TAP);
        // controller position
        this.mControllerPos = new Point();
        this.mControllerPos.x = screen.x-(CharacterController.CONTROLLER_SIZE_BG.x+20);
        this.mControllerPos.y = screen.y-(CharacterController.CONTROLLER_SIZE_BG.y+20);
        // player
        this.mSwimmer = new SeaPlayer(this.mContext,this.mImage);
        // controller
        this.mController = new CharacterController(this.mContext,this.mImage);
        // enemy and each items
        this.mChara = new BaseCharacter[6];
        for (int i = 0; i < this.mChara.length; i++) this.mChara[i] = new BaseCharacter(this.mImage);
        // animation for the enemy
        this.mAni = new Animation[2];
        for (int i = 0; i < this.mAni.length; i++) this.mAni[i] = new Animation();
        // loading the file for enemy
        this.mChara[0].LoadCharaImage(this.mContext, "shark");
        // for items
        for (int i = 1; i < this.mChara.length-1; i++) this.mChara[i].LoadCharaImage(this.mContext,"seaitems");
        // for light
        this.mChara[5].LoadCharaImage(this.mContext,"sealight");
        // image setting
        // shark
        this.mChara[0].mSize.x = SeaEnemyManager.SHARK_SIZE.x;
        this.mChara[0].mSize.y = SeaEnemyManager.SHARK_SIZE.y;
        this.mChara[0].mPos.x = (screen.x-this.mChara[0].mSize.x)>>1;
        this.mChara[0].mPos.y = 200;
        // animation setting
        this.mAni[0].SetAnimation(
                0,0,
                this.mChara[0].mSize.x,
                this.mChara[0].mSize.y,
                SeaEnemyManager.ANIMATION_COMMON_COUNT_MAX,
                SeaEnemyManager.ANIMATION_COMMON_FRAME,0);
        // items
        for (int i = 1; i < this.mChara.length; i++) {
            this.mChara[i].mSize.x = SeaItem.ITEM_SIZE.x;
            this.mChara[i].mSize.y = SeaItem.ITEM_SIZE.y;
            this.mChara[i].mOriginPos.y = SeaItem.ITEM_SIZE.y*(i-1);
            this.mChara[i].mPos.y = 200+((SeaItem.ITEM_SIZE.y+10)*(i-1));
            this.mChara[i].mPos.x = 200;
        }
        // light
        this.mChara[5].mSize.x = SeaStage.LIGHTNING_SIZE.x;
        this.mChara[5].mSize.y = SeaStage.LIGHTNING_SIZE.y;
        this.mChara[5].mPos.x = (screen.x-this.mChara[5].mSize.x)>>1;
        this.mChara[5].mPos.y = 200;
        // animation setting
        this.mAni[1].SetAnimation(
                0,0,
                this.mChara[5].mSize.x,
                this.mChara[5].mSize.y,
                SeaStage.ANIMATION_LIGHTNING_COUNT_MAX,
                SeaStage.ANIMATION_LIGHTNING_FRAME,0);

        // to call initialization functions
        this.mSwimmer.InitForTheBrief();

        this.mController.InitController(
                this.mControllerPos.x,
                this.mControllerPos.y);
    }
    /***************************************************************************************
     Each update functions
     *************************************************************************************/
    /*
        Update
    */
    public int Update() {

        // When response is nothing
        if (this.mResponse == -1) {
            // Update the stage from divergence
            switch(this.mBriefTheStage) {
                case Play.STAGE_OFF_ROAD:
                    this.UpdateOffroad();
                    break;
                case Play.STAGE_ROAD:
                    this.UpdateRoad();
                    break;
                case Play.STAGE_SEA:
                    this.UpdateSea();
                    break;
            }
            // Update Talk
            this.mTalk.UpdateTalk();
            // Update explanation index
            if (this.mBriefIndex < this.mBriefIndexMax) {
                // when the flag that is waiting reading is true, to show OK button
                if (!this.mOK.mExistFlag && this.mTalk.GetWaitingFlag()) {
                    // to show accept button
                    this.mOK.mExistFlag = true;
                } else if (this.mOK.mExistFlag && this.mTalk.GetWaitingFlag()) {
                    // when pressed accept button, the explanation to next.
                    if (Collision.CheckTouch(
                            this.mOK.mPos.x, this.mOK.mPos.y,
                            this.mOK.mSize.x, this.mOK.mSize.y,
                            this.mOK.mScale)) {
                        // set index that explain the brief stage
                        this.mBriefIndex = this.mBriefIndexNext;
                        // not to show accept button
                        this.mOK.mExistFlag = false;
                        // explanation to next
                        this.mBriefIndexNext++;

                        // to restart reading text that explain the stage.
                        this.mTalk.SetRestartReading(true);

                        // when index reached to final index, to show yes and no.
                        if (this.mBriefIndex == this.mBriefIndexMax) {
                            for (BaseCharacter ch : this.mAnswer) ch.mExistFlag = true;
                            this.mBriefIndexNext = this.mBriefIndex;
                        }
                    }
                }

            } else if (this.mBriefIndex == this.mBriefIndexMax){
                // to answer, yes or no
                for (int i = 0; i < 2; i++) {
                    if (this.mAnswer[i].mExistFlag) {
                        if (Collision.CheckTouch(
                                this.mAnswer[i].mPos.x, this.mAnswer[i].mPos.y,
                                this.mAnswer[i].mSize.x, this.mAnswer[i].mSize.y,
                                this.mAnswer[i].mScale)) {
                            // if response is yes, transition to play scene.
                            // in the no, to back first explanation
                            this.mResponse = i;
                            // not to show response
                            for (BaseCharacter ch: this.mAnswer) ch.mExistFlag = false;
                            // play SE
                            this.mSound[i].PlaySE();
                            break;
                        }
                    }
                }
            }
            // when got the response, to do process based on the response
        } else {
            // make interval time a while
            this.mIntervalTime++;
            // the count time more than fixed time, to do
            if (INTERVAL_TIME <= this.mIntervalTime) {
                // in the Yes, to transition to scene
                if (this.mResponse == 0) {
                    Wipe.CreateWipe(SceneManager.SCENE_PLAY, Wipe.TYPE_PENETRATION);
                    return SCENE_RELEASE;
                    // in the No, to be beginning.
                } else if (this.mResponse == 1) {
                    this.mBriefIndex = 0;
                    this.mBriefIndexNext = 1;
                    this.mResponse = -1;
                    // reset count time
                    this.mIntervalTime = 0;
                    // the explanation back to beginning position
                    this.mTalk.SetBackToBeginning();
                }
            }
        }

        // Update Menu
        if (this.mMenu.UpdateMenu()) return SCENE_RELEASE;
        // Play background music
        this.mSound[0].PlayBGM(this.mBGMfileName);

        return Scene.SCENE_MAIN;
    }
    /*
        Update character to the off-road
    */
    private void UpdateOffroad() {

        // player's move
        if (this.mChara[0].mExistFlag) {
            this.mChara[0].CharacterMoveToTouchedPosition(OffroadPlayer.PLAYER_DEFAULT_SPEED);
            // constrain move area
            this.mChara[0].ConstrainMove();
        }
        // to diverge process from brief index.
        switch(this.mBriefIndex) {
            case 1:             // to show rock
                if (!this.mChara[1].mExistFlag) this.mChara[1].mExistFlag = true;
                break;
            case 2:             // to show jump point
                if (!this.mChara[2].mExistFlag) this.mChara[2].mExistFlag = true;
                if (this.mChara[1].mExistFlag) this.mChara[1].mExistFlag = false;
                break;
            case 4:
            case 3:             // to show command
                if (!this.mChara[3].mExistFlag) this.mChara[4].mExistFlag = this.mChara[3].mExistFlag = true;
                if (this.mChara[2].mExistFlag) this.mChara[2].mExistFlag = false;
                // update input image that change scale variable.
                if (this.mChara[4].mExistFlag) {
                    if (0.1f < this.mChara[4].mScale) {
                        this.mChara[4].mScale -= 0.01f;
                    } else if (this.mChara[4].mScale <= 0.1f) {
                        this.mChara[4].mScale = ActionCommand.INPUT_STARTING_SCALE;
                    }
                    // update command image that change origin position.
                    float difference = this.mChara[3].mScale - this.mChara[4].mScale;
                    if (Math.abs(difference) <= 0.2f) {
                        // change the image that command.
                        this.mChara[3].mOriginPos.y = this.mChara[3].mSize.y;
                    } else {
                        this.mChara[3].mOriginPos.y = this.mChara[3].mSize.y*2;
                    }
                }
                break;
            case 5:             // not to show command
                if (this.mChara[3].mExistFlag) this.mChara[4].mExistFlag = this.mChara[3].mExistFlag = false;
                break;
        }
        // update each characters' animation
        // Animation class
        for (int i = 0; i < this.mAni.length; i++) {
            if (this.mChara[i].mExistFlag)
                this.mAni[i].UpdateAnimation(this.mChara[i].mOriginPos, false);
        }
    }
    /*
        Update road    
    */
    private void UpdateRoad() {
        // update player
        this.mRoadPlayer.UpdateToExplain();
        // competitor
        this.mCompetitorManager.UpdateToExplain();
        // to diverge process from brief index.
        switch(this.mBriefIndex) {
            case 0:     // to explain how to move
                // reset the player's stamina
                RoadPlayer.SetStamina(RoadPlayer.STAMINA_MAX);
                break;
            case 1:     // to explain how to jump action
                // to show finger to explain the action.
                this.mEffect[0].ToShowTheEffect();
                Point playerPos = RoadPlayer.GetPosition();
                // update the effect
                this.mEffect[0].UpdateCharacterEffect(
                        (float)playerPos.x+(RoadRunner.RUNNER_SIZE.x>>1),(float)playerPos.y,
                        RoadRunner.RUNNER_SIZE.x,RoadRunner.RUNNER_SIZE.y,
                        1.0f,false);
                break;
            case 2:     // to show competitor
                RoadCompetitorManager.SetExist(true);
                Point comPos[] = RoadCompetitorManager.GetPosition();
                // change the position to finger
                this.mEffect[0].UpdateCharacterEffect(
                        (float)comPos[0].x+(RoadRunner.RUNNER_SIZE.x>>1),(float)comPos[0].y,
                        RoadRunner.RUNNER_SIZE.x,RoadRunner.RUNNER_SIZE.y,
                        1.0f,false);
                break;
            case 3:     // to explain attention to the competitor's attack.
                // not to show the effect
                this.mEffect[0].NotToShowTheEffect();
                RoadCompetitorManager.SetTemporaryActionType(RoadRunner.ACTION_PREPARE_ATTACK);
                break;
            case 4:     // to explain the common obstacles.
                // not to show competitors
                RoadCompetitorManager.SetExist(false);
                RoadCompetitorManager.SetTemporaryActionType(RoadRunner.ACTION_RUN);
                // to show cone
                this.mChara[2].mExistFlag = true;
                break;
            case 5:     // to explain the obstacles, especially hurdle.
                // not to show cone
                this.mChara[2].mExistFlag = false;
                // to show the hurdles
                this.mChara[1].mExistFlag = true;
                break;
            case 6:     // to explain the character's effect
                // to show the effects
                this.mChara[0].mExistFlag = true;
                // update the effect's position
                this.mChara[0].mPos.x = RoadPlayer.GetPosition().x + this.mChara[0].mSize.x;
                this.mChara[0].mPos.y = RoadPlayer.GetPosition().y;
                break;
            case 7:
                // not to show effects and hurdles
                this.mChara[1].mExistFlag = this.mChara[0].mExistFlag = false;
            case 8:     // to explain the game-over
                // decrease the player's stamina
                RoadPlayer.SetStamina(0);
                break;
        }
    }
    /*
        Update sea
    */
    private void UpdateSea() {

        // to update controller
        FPoint move = this.mController.UpdateController();
        int itemType = SeaItem.ITEM_EMPTY;
        // the position to show the finger
        FPoint fingerPos = new FPoint();
        Point size = new Point();
        // to diverge process from brief index.
        switch (this.mBriefIndex) {
            case 0:         // how to move
            case 1:
                // to draw the finger
                this.mEffect[0].ToShowTheEffect();
                size.x = CharacterController.CONTROLLER_SIZE_BG.x;
                size.y = CharacterController.CONTROLLER_SIZE_BG.y;
                fingerPos.x = (float)this.mControllerPos.x+(size.x>>1);
                fingerPos.y = (float)this.mControllerPos.y+(size.y>>1);
                break;
            case 2:         // how to attack
                // finger moves to the attack button
                size.x = SeaPlayer.ATTACK_BUTTON_SIZE.x;
                size.y = SeaPlayer.ATTACK_BUTTON_SIZE.y;
                fingerPos.x = SeaPlayer.ATTACK_BUTTON_POSITION.x+(size.x>>1);
                fingerPos.y = SeaPlayer.ATTACK_BUTTON_POSITION.y+(size.y>>1);
                break;
            case 3:         // to explain the special attack
                // finger moves to the button
                size.x = SeaPlayer.ATTACK_IMAGE_SIZE.x;
                size.y = SeaPlayer.ATTACK_IMAGE_SIZE.y;
                fingerPos.x = SeaPlayer.ATTACK_IMAGE_POSITION.x+(size.x>>1);
                fingerPos.y = SeaPlayer.ATTACK_IMAGE_POSITION.y+(size.y>>1);
                // filling the gage to do special attack
                this.mSwimmer.ToIncreaseTheAttackGauge(true);
                break;
            case 4:         // to show the enemy to explain the attack gauge
                // not to show the finger
                this.mEffect[0].NotToShowTheEffect();
                this.mChara[0].mExistFlag = true;
                break;
            case 5:         // to show each items to explain each items.
                this.mChara[0].mExistFlag = false;
                for (int i = 1; i < this.mChara.length-1; i++) {
                    this.mChara[i].mExistFlag = true;
                }
                break;
            case 6:         // to explain using the item
                // to show the finger
                this.mEffect[0].ToShowTheEffect();
                size.x = SeaPlayer.ITEM_BOX_SIZE.x;
                size.y = SeaPlayer.ITEM_BOX_SIZE.y;
                fingerPos.x = SeaPlayer.ITEM_BOX_POSITION.x+(size.x>>1);
                fingerPos.y = SeaPlayer.ITEM_BOX_POSITION.y+(size.y>>1);
                itemType = SeaItem.ITEM_SPEED_UP;
                break;
            case 7:         // to show lighting
                // not to show items and finger
                this.mEffect[0].NotToShowTheEffect();
                for (int i = 1; i < this.mChara.length-1; i++) {
                    this.mChara[i].mExistFlag = false;
                }
                // to show the lighting
                this.mChara[5].mExistFlag = true;
                break;
            case 8:         // not to show the light
                this.mChara[5].mExistFlag = false;
        }
        // to update player
        this.mSwimmer.UpdateForTheBrief(move,itemType);
        // to update the finger action
        this.mEffect[0].UpdateCharacterEffect(
                fingerPos.x,fingerPos.y,
                size.x, size.y,
                1.0f,false);
        // update animation
        int index[] = {0,5};        // shark and light
        for (int i = 0; i < this.mAni.length; i++) {
            if (this.mChara[index[i]].mExistFlag) {
                this.mAni[i].UpdateAnimation(this.mChara[index[i]].mOriginPos,false);
            }
        }
    }
    /********************************************************************************
        Each drawing functions
    *********************************************************************************/
    /*
        Draw
    */
    public void Draw() {
        
        // when response is nothing, to do
        if (this.mResponse == -1) {
            // diverge drawing process from current stage number
            switch(this.mBriefTheStage) {
                case Play.STAGE_OFF_ROAD:
                    this.DrawOffRoad();
                    break;
                case Play.STAGE_ROAD:
                    this.DrawRoad();
                    break;
                case Play.STAGE_SEA:
                    this.DrawSea();
                    break;
            }

            // Explain how to play
            this.mTalk.DrawTalk(0,0);

            // Accept button
            if (this.mOK.mExistFlag) {
                this.mImage.DrawScale(
                        this.mOK.mPos.x,
                        this.mOK.mPos.y,
                        this.mOK.mSize.x,
                        this.mOK.mSize.y,
                        this.mOK.mOriginPos.x,
                        this.mOK.mOriginPos.y,
                        this.mOK.mScale,
                        this.mOK.mBmp
                );
            }
            // Yes and No
            for (int i = 0; i < 2; i++) {
                if (this.mAnswer[i].mExistFlag) {
                    this.mImage.DrawScale(
                            this.mAnswer[i].mPos.x,
                            this.mAnswer[i].mPos.y,
                            this.mAnswer[i].mSize.x,
                            this.mAnswer[i].mSize.y,
                            this.mAnswer[i].mOriginPos.x,
                            this.mAnswer[i].mOriginPos.y,
                            this.mAnswer[i].mScale,
                            this.mAnswer[i].mBmp
                    );
                }
            }
            // Menu that is back button
            this.mMenu.DrawMenu();
        } else if (this.mResponse == 0){
            // good luck
            this.mImage.drawText("Good luck!",this.mExplanationPos.x,this.mExplanationPos.y,32,Color.YELLOW);
        }
    }
    /*
        Off-road
    */
    private void DrawOffRoad(){
        // Except for player, each characters
        for (int i = 1; i < this.mChara.length; i++) {
            if (this.mChara[i].mExistFlag) {
                this.mImage.DrawAlphaAndScale(
                        this.mChara[i].mPos.x,
                        this.mChara[i].mPos.y,
                        this.mChara[i].mSize.x,
                        this.mChara[i].mSize.y,
                        this.mChara[i].mOriginPos.x,
                        this.mChara[i].mOriginPos.y,
                        this.mChara[i].mAlpha,
                        this.mChara[i].mScale,
                        this.mChara[i].mBmp
                );
            }
        }
        // Player character
        if (this.mChara[0].mExistFlag) {
            this.mImage.DrawScale(
                    this.mChara[0].mPos.x,
                    this.mChara[0].mPos.y,
                    this.mChara[0].mSize.x,
                    this.mChara[0].mSize.y,
                    this.mChara[0].mOriginPos.x,
                    this.mChara[0].mOriginPos.y,
                    this.mChara[0].mScale,
                    this.mChara[0].mBmp
            );
        }
    }

    /*
        Draw road
    */
    private void DrawRoad() {
        // Each obstacles
        String text[] = {"ただの障害物","無敵", "速度上昇", "回復"};
        int colour[] = {Color.WHITE,Color.RED,Color.BLUE,Color.GREEN};
        // hurdle
        for (int i = 0; i < RoadObstacles.HURDLE_COLOR_KIND; i++) {
            if (this.mChara[1].mExistFlag) {
                int y = this.mChara[1].mPos.y+(this.mChara[1].mSize.y+25)*i;
                this.mImage.DrawScale(
                        this.mChara[1].mPos.x,
                        y,
                        this.mChara[1].mSize.x,
                        this.mChara[1].mSize.y,
                        this.mChara[1].mOriginPos.x,
                        this.mChara[1].mOriginPos.y+(this.mChara[1].mSize.y*i),
                        this.mChara[1].mScale,
                        this.mChara[1].mBmp
                );
                // text that explain the effect
                this.mImage.drawText(
                        text[i],
                        this.mChara[1].mPos.x+(this.mChara[1].mSize.x<<1),
                        y+this.mChara[1].mSize.y,
                        21,colour[i]);
            }
        }
        // Cone
        if (this.mChara[2].mExistFlag) {
            this.mImage.DrawScale(
                    this.mChara[2].mPos.x,
                    this.mChara[2].mPos.y,
                    this.mChara[2].mSize.x,
                    this.mChara[2].mSize.y,
                    this.mChara[2].mOriginPos.x,
                    this.mChara[2].mOriginPos.y,
                    this.mChara[2].mScale,
                    this.mChara[2].mBmp
            );
        }
        // competitor
        this.mCompetitorManager.DrawToExplain();
        // player
        this.mRoadPlayer.DrawToExplain();
        // ball effect
        for (int i = 0; i < 2; i++) {
            if (this.mChara[0].mExistFlag) {
                this.mImage.DrawScale(
                        this.mChara[0].mPos.x+(this.mChara[0].mSize.x+10)*i,
                        this.mChara[0].mPos.y,
                        this.mChara[0].mSize.x,
                        this.mChara[0].mSize.y,
                        this.mChara[0].mOriginPos.x,
                        this.mChara[0].mOriginPos.y+(this.mChara[0].mSize.y*i),
                        this.mChara[0].mScale,
                        this.mChara[0].mBmp
                );
            }
        }
        // finger effect
        this.mEffect[0].DrawCharacterEffect();
    }
    /*
        Draw for sea
    */
    private void DrawSea() {
        // the text to explain the item.
        String text[] = {
            "無敵になります。",
            "速度上昇",
            "速度減少",
            "スーパーな力を発揮できます。"
        };
        int color[] = {
            Color.RED,
            Color.CYAN,
            Color.WHITE,
            Color.YELLOW
        };
        // fill back color with blue
        Point screen = GameView.GetScreenSize();
        this.mImage.fillRect(0,0,screen.x,screen.y,Color.BLUE);
        // enemy and items
        for (BaseCharacter ch: this.mChara) {
            if (ch.mExistFlag) {
                this.mImage.DrawImage(
                    ch.mPos.x,ch.mPos.y,
                    ch.mSize.x,ch.mSize.y,
                    ch.mOriginPos.x,ch.mOriginPos.y,
                    ch.mBmp);
            }
        }
        // to explain the item
        for (int i = 0; i < text.length; i++) {
            if (this.mChara[i+1].mExistFlag) {
                this.mImage.drawText(
                        text[i],
                        this.mChara[i+1].mPos.x+this.mChara[i+1].mSize.x+20,
                        this.mChara[i+1].mPos.y+(this.mChara[i+1].mSize.y>>1)+5,
                        21,color[i]);
            }
        }
        // player
        this.mSwimmer.DrawForTheBrief();
        // controller
        this.mController.DrawController();
        // finger effect
        this.mEffect[0].DrawCharacterEffect();
    }

    /*
        Release
    */
    public int Release() {
        this.mContext = null;         // activity
        this.mImage = null;            // image object
        // Base Character class
        if (this.mChara != null) {
            for (int i = 0; i < this.mChara.length; i++) {
                this.mChara[i].ReleaseCharaBmp();
                this.mChara[i] = null;
            }
        }
        // Road player
        if (this.mRoadPlayer != null) {
            this.mRoadPlayer.ReleaseRoadPlayer();
            this.mRoadPlayer = null;
        }
        // competitor
        if (this.mCompetitorManager != null) {
            this.mCompetitorManager.ReleaseCompetitorManager();
            this.mCompetitorManager = null;
        }
        // Effect
        if (this.mEffect != null) {
            for (int i = 0; i < this.mEffect.length; i++) {
                this.mEffect[i].ReleaseCharacterEffect();
                this.mEffect[i] = null;
            }
        }
        // Sea
        // swimmer
        if (this.mSwimmer != null) {
            this.mSwimmer.ReleaseSwimmer();
            this.mSwimmer = null;
        }
        // controller
        if (this.mController != null) {
            this.mController.ReleaseController();
            this.mController = null;
        }

        // Animation class
        if (this.mAni != null) {
            for (int i = 0; i < this.mAni.length; i++) {
                this.mAni[i] = null;
            }
        }
        // Sound class
        for (int i = 0; i < this.mSound.length; i++) {
            this.mSound[i].StopBGM();
            this.mSound[i] = null;
        }
        // Menu class
        this.mMenu.ReleaseMenu();
        // Accept button
        this.mOK.ReleaseCharaBmp();
        this.mOK = null;
        // Response buttons
        for (int i = 0; i < this.mAnswer.length; i++) {
            this.mAnswer[i].ReleaseCharaBmp();
            this.mAnswer[i] = null;
        }
        // Talk
        this.mTalk = null;

        return Scene.SCENE_END;
    }
}