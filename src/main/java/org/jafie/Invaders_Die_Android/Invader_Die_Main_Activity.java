package org.jafie.Invaders_Die_Android;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.FixedResolutionPolicy;
import org.andengine.engine.options.resolutionpolicy.RelativeResolutionPolicy;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.adt.io.in.IInputStreamOpener;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;
import org.andengine.engine.camera.ZoomCamera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.IOnSceneTouchListener;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.ButtonSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXLoader.ITMXTilePropertiesListener;
import org.andengine.extension.tmx.TMXObject;
import org.andengine.extension.tmx.TMXObjectGroup;
import org.andengine.extension.tmx.TMXProperties;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTileProperty;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.extension.tmx.util.exception.TMXLoadException;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.detector.PinchZoomDetector;
import org.andengine.input.touch.detector.PinchZoomDetector.IPinchZoomDetectorListener;
import org.andengine.input.touch.detector.ScrollDetector;
import org.andengine.input.touch.detector.ScrollDetector.IScrollDetectorListener;
import org.andengine.input.touch.detector.SurfaceScrollDetector;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.bitmap.BitmapTexture;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TextureRegionFactory;
import org.andengine.opengl.texture.region.TiledTextureRegion;

import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.net.wifi.p2p.WifiP2pInfo;
import android.util.Log;
import android.widget.Toast;

public class Invader_Die_Main_Activity extends SimpleBaseGameActivity implements
		IPinchZoomDetectorListener, IScrollDetectorListener,
		IOnSceneTouchListener {

	private static final int CAMERA_WIDTH = 800;		//The initial user view
	private static final int CAMERA_HEIGHT = 480;
	private static final int NBMONSTERPOP = 6;			//Nombers of monster pop

	// SPRITES
	protected ITextureRegion mWoodbox, mTower1, mTower2, mCircle, mHeadbone,
			mFireball, mArrow;			//The textures

	private ButtonSprite woodBxMenu;		//The menu sprite
	private BitmapTextureAtlas texWolf;		//The monster textures
	private TiledTextureRegion regWolf;		//The manager of monster animation
	private PopupMenu popup;				//The cursor sprite
	private float Widthbox, Heigthbox;		
	private Font loading;
	private Text loadertext;

	
	private ArrayList<Way> ArrayWay = new ArrayList<Way>();		//The list of the ways
	private ArrayList<Rectangle> ArrayWall = new ArrayList<Rectangle>();	//List of the walls
	private ArrayList<Sprite> ArraySpawn = new ArrayList<Sprite>();		//List of the spawns
	private ArrayList<Rectangle> ArrayCastle = new ArrayList<Rectangle>();	//List of the castles

	// Arrayplayer
	private ArrayList<Tower> ArrayTowerAlly = new ArrayList<Tower>();		//List of the allies towers
	private ArrayList<Enemy> ArrayEnemy = new ArrayList<Enemy>();			//List of the enemies

	// Arrayenemy
	protected ArrayList<Tower> ArrayTowerEnemy = new ArrayList<Tower>();		//List of the enemies tower
	private ArrayList<Enemy> ArrayAlly = new ArrayList<Enemy>();			//List of the allies

	// CAMERA MANAGEMENT
	private Object mPinchZoomStartedCameraZoomFactor;	//Used to detect that a zoom is processed	
	private ZoomCamera mZoomCamera;
	private SurfaceScrollDetector mScrollDetector;		//Detect a scroll
	private PinchZoomDetector mPinchZoomDetector;		//Detect a zoom
	private float factoractualzoom = 1;

	// Mapdata MANAGEMENT
	private float downedX, downedY, upedX, upedY; //Used about the sensibility of the zoom/scroll
	private TMXTiledMap tiledMap;	//map loaded thanks to the tmx file
	private TMXLayer tmxLayer;		//Layer loaded from the tmx file
	protected int mCactusCount;

	// OTHERS
	protected Scene scene;		//Scene of game
	private int timer = 0;		//Time about the scroll sensibility
	private int timerbetwennmonster = 0;	//Timer about the pop between each monster
	private int timerwave = 0;			//Timer between each wave of monster
	private float blink = 0;		//Blink of the spawn points
	private boolean blinkminus = false;	//Blink of the spawn points
	private int spawnumber = 0;	//Manage the spawn where the monsters pop
	private int castlelife = 5;		//Life of the player (the castle)
	private int monsterpop;
	private int monsterpopmax;
	private int TIMER_WAVE = 600;		//Timer set between each wave
	private int TIMER_MONSTER = 30;		//Timer set between each monsters
	
	
	
	private static SocketManager socketManager;
	private static WifiP2pInfo info;
	private static ProgressDialog progressDialog;

	// -------------------------------METHODS---------------------------------------

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		//socketManager.disconnectSockets();
		finish();
	}
	
	@Override
	public EngineOptions onCreateEngineOptions() {

		// TODO : Multiplayer feature removed - To reimplement
		/*info = (WifiP2pInfo) getIntent().getParcelableExtra("info");
		
		progressDialog = ProgressDialog.show(this, "Launch Game",
				 "Please wait...", true, false);

		socketManager = new SocketManager(this, info, progressDialog);
		if (info.groupFormed) {
			if (info.isGroupOwner) {
				socketManager.connectOwner();
			} else {
				socketManager.connectNoOwner();
			}
		}*/
		
		//while(progressDialog.isShowing());

		// CAMERA ZOOM TEST//
		this.mZoomCamera = new ZoomCamera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

		final EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE_FIXED, new FillResolutionPolicy(), this.mZoomCamera);

		return engineOptions;
	}

	@Override
	protected void onCreateResources() {
		try {

			// 1 - Set up bitmap textures

			ITexture caseselected = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/case1.png");
						}
					});
			ITexture woodbox = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/wood_box.png");
						}
					});

			ITexture tower1 = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/Tower1.png");
						}
					});
			ITexture tower2 = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets()
									.open("gfx/the_pixel_tower_of_babel_by_atskaheart-d4hffz4.png");
						}
					});
			ITexture circle = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/circle.png");
						}
					});
			ITexture headbone = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/headbone.png");
						}
					});

			ITexture fireball = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/Fireball.png");
						}
					});

			ITexture Arrow = new BitmapTexture(this.getTextureManager(),
					new IInputStreamOpener() {
						@Override
						public InputStream open() throws IOException {
							return getAssets().open("gfx/arrow.png");
						}
					});
			
			final BitmapTextureAtlas fontTexture = new BitmapTextureAtlas(this.getTextureManager(),256, 256, TextureOptions.BILINEAR);

			this.getEngine().getTextureManager().loadTexture(fontTexture);
			loading = new Font(this.getFontManager(), fontTexture, Typeface.create(Typeface.DEFAULT, Typeface.NORMAL), 24, true, Color.WHITE);
			this.getEngine().getFontManager().loadFont(loading);


			// Texture monsters
			texWolf = new BitmapTextureAtlas(this.getTextureManager(), 384,
					256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
			regWolf = BitmapTextureAtlasTextureRegionFactory
					.createTiledFromAsset(texWolf, this.getAssets(),
							"gfx/monster.png", 0, 0, 12, 8);
			texWolf.load();

			// Generate Sprites
			// 2 - Load bitmap textures into VRAM
			caseselected.load();
			woodbox.load();
			tower1.load();
			tower2.load();
			circle.load();
			headbone.load();
			fireball.load();
			Arrow.load();

			// 3 - Set up texture regions
			this.mWoodbox = TextureRegionFactory.extractFromTexture(woodbox);
			this.mTower1 = TextureRegionFactory.extractFromTexture(tower1);
			this.mTower2 = TextureRegionFactory.extractFromTexture(tower2);
			this.mCircle = TextureRegionFactory.extractFromTexture(circle);
			this.mHeadbone = TextureRegionFactory.extractFromTexture(headbone);
			this.mFireball = TextureRegionFactory.extractFromTexture(fireball);
			this.mArrow = TextureRegionFactory.extractFromTexture(Arrow);

		} catch (IOException e) {
			Debug.e(e);
		}

	}

	@Override
	protected Scene onCreateScene() {

		// 1 - Create new scene
		scene = new Scene();

		// MAP AND OBJECT LOADER

		loadertext= new Text(500, 800, loading , "LOADING...",getVertexBufferObjectManager());

		scene.attachChild(loadertext);

		/*Toast.makeText(
				this,
				"Loading gaming scene",
				Toast.LENGTH_LONG).show();*/

		managementtmx();

		/*Toast.makeText(
				this,
				"Gaming scene loaded!!!",
				Toast.LENGTH_LONG).show();*/

		// Create the menu

		woodBxMenu = new ButtonSprite(0, (float) 0, this.mWoodbox,
				getVertexBufferObjectManager()) {
			@Override
			public boolean onAreaTouched(TouchEvent pTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {
				if (pTouchEvent.isActionDown()) {

					// CREATE A NEW TOWER
					switch (popup.testTower(pTouchAreaLocalX,
							mZoomCamera.getZoomFactor())) {
					case 1:
						String sendTower1 = "<;" + popup.getX() + ";"
								+ popup.getY() + ";" + "1" + ";>";
						//socketManager.sendData(sendTower1);
						ArrayTowerAlly.add(new Tower(popup.getX() + 1, popup
								.getY() + 1, mTower1,
								getVertexBufferObjectManager(), 1, mCircle,
								scene, mArrow));
						break;

					case 2:
						String sendTower2 = "<;" + popup.getX() + ";"
								+ popup.getY() + ";" + "2" + ";>";
						//.sendData(sendTower2);
						ArrayTowerAlly.add(new Tower(popup.getX() + 1, popup
								.getY() + 1, mTower2,
								getVertexBufferObjectManager(), 2, mCircle,
								scene, mFireball));
						break;

					default:
						break;

					}

					popup.testZone(ArrayTowerAlly, ArrayWall, scene);
				}

				return super.onAreaTouched(pTouchEvent, pTouchAreaLocalX,
						pTouchAreaLocalY);
			}
		};

		// Create the cursor
		popup = new PopupMenu((float) 0, (float) 0, (float) 64, (float) 64,
				getVertexBufferObjectManager(), tmxLayer, true) {
			public boolean onAreaTouched(TouchEvent pSceneTouchEvent,
					float pTouchAreaLocalX, float pTouchAreaLocalY) {

				return true;
			}
		};

		

		Widthbox = CAMERA_WIDTH;
		Heigthbox = CAMERA_HEIGHT / 5;
		woodBxMenu.setWidth(CAMERA_WIDTH);
		woodBxMenu.setHeight(CAMERA_HEIGHT / 5);
		woodBxMenu.setPosition(mZoomCamera.getCenterX() - (CAMERA_WIDTH / 2)
				/ (mZoomCamera.getZoomFactor()), mZoomCamera.getCenterY()
				+ (CAMERA_HEIGHT / 3 - 10) / (mZoomCamera.getZoomFactor()));
		// Set the opacity to the sprite
		//Set the transparency of the cursor
		float OPACITYCURSOR = (float) 0.4;
		popup.setAlpha(OPACITYCURSOR);
		// Attach the sprites on the scene
		
		// 6 - Add touch handlers
		scene.registerTouchArea(woodBxMenu);
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		// CAMERA SET SCROLL AND ZOOM
		this.mScrollDetector = new SurfaceScrollDetector(this);
		this.mPinchZoomDetector = new PinchZoomDetector(this);

		// SET UPDATE FUNCTION
		scene.detachChild(loadertext);
		scene.attachChild(this.tiledMap.getTMXLayers().get(0)); //PUT THE MAP
		Sceneupdate();
		scene.setOnSceneTouchListener(this);
		scene.setTouchAreaBindingOnActionDownEnabled(true);

		return scene;
	}

	// ------------- CAMERA SCROOL MANAGEMENT------------------------

	public void scrollManagement(float pDistanceX, float pDistanceY) {
		final float zoomFactor = this.mZoomCamera.getZoomFactor();
		this.mZoomCamera.offsetCenter(-pDistanceX / zoomFactor, -pDistanceY
				/ zoomFactor);

		if (this.mZoomCamera.getCenterX() < 80) {
			mZoomCamera.setCenter(80, mZoomCamera.getCenterY());
		}
		if (this.mZoomCamera.getCenterX() > tmxLayer.getWidth()) {
			mZoomCamera
					.setCenter(tmxLayer.getWidth(), mZoomCamera.getCenterY());
		}
		if (this.mZoomCamera.getCenterY() < 40) {
			mZoomCamera.setCenter(mZoomCamera.getCenterX(), 40);
		}
		if (this.mZoomCamera.getCenterY() > tmxLayer.getHeight()) {
			mZoomCamera.setCenter(mZoomCamera.getCenterX(),
					tmxLayer.getHeight());
		}
		woodBxMenu.setPosition(mZoomCamera.getCenterX() - (CAMERA_WIDTH / 2)
				/ (mZoomCamera.getZoomFactor()), mZoomCamera.getCenterY()
				+ (CAMERA_HEIGHT / 3 - 10) / (mZoomCamera.getZoomFactor()));

	}

	@Override
	public void onScrollStarted(final ScrollDetector pScollDetector,
			final int pPointerID, final float pDistanceX, final float pDistanceY) {

		scrollManagement(pDistanceX, pDistanceY);
	}

	@Override
	public void onScroll(final ScrollDetector pScollDetector,
			final int pPointerID, final float pDistanceX, final float pDistanceY) {

		scrollManagement(pDistanceX, pDistanceY);
	}

	@Override
	public void onScrollFinished(final ScrollDetector pScollDetector,
			final int pPointerID, final float pDistanceX, final float pDistanceY) {
		scrollManagement(pDistanceX, pDistanceY);

	}

	// -------------------------ZOOM MANAGEMENT-----------------------------

	public void zoommanagement(float pZoomFactor) {
		//Minimal factor of the zoom
		float MINZOOMFACTOR = (float) 0.6;
		if ((pZoomFactor * factoractualzoom) < MINZOOMFACTOR) {
			mZoomCamera.setZoomFactor((float) MINZOOMFACTOR);
		} else {
			//Maximal factor of the room
			float MAXZOOMFACTOR = 2;
			if ((pZoomFactor * factoractualzoom) > MAXZOOMFACTOR) {
				mZoomCamera.setZoomFactor((float) MAXZOOMFACTOR);
			} else {
				mZoomCamera.setZoomFactor(factoractualzoom * pZoomFactor);

			}
		}
		woodBxMenu.setSize(Widthbox / (mZoomCamera.getZoomFactor()), Heigthbox
				/ ((mZoomCamera.getZoomFactor())));
		woodBxMenu.setPosition(mZoomCamera.getCenterX() - (CAMERA_WIDTH / 2)
				/ (mZoomCamera.getZoomFactor()), mZoomCamera.getCenterY()
				+ (CAMERA_HEIGHT / 3 - 10) / (mZoomCamera.getZoomFactor()));
	}

	@Override
	public void onPinchZoomStarted(final PinchZoomDetector pPinchZoomDetector,
			final TouchEvent pTouchEvent) {
		this.mPinchZoomStartedCameraZoomFactor = this.mZoomCamera
				.getZoomFactor();
		factoractualzoom = (Float) this.mPinchZoomStartedCameraZoomFactor;
	}

	@Override
	public void onPinchZoom(final PinchZoomDetector pPinchZoomDetector,
			final TouchEvent pTouchEvent, final float pZoomFactor) {

		zoommanagement(pZoomFactor);
	}

	@Override
	public void onPinchZoomFinished(final PinchZoomDetector pPinchZoomDetector,
			final TouchEvent pTouchEvent, final float pZoomFactor) {
		zoommanagement(pZoomFactor);

	}

	// --------------------TOUCH MANAGEMENT -----------------------------
	@Override
	public boolean onSceneTouchEvent(final Scene pScene,
			final TouchEvent pSceneTouchEvent) {
		this.mPinchZoomDetector.onTouchEvent(pSceneTouchEvent);

		if (this.mPinchZoomDetector.isZooming()) {
			this.mScrollDetector.setEnabled(false);
		} else {
			if (pSceneTouchEvent.isActionDown()) {
				this.mScrollDetector.setEnabled(true);
			}
			this.mScrollDetector.onTouchEvent(pSceneTouchEvent);
		}
		timer++;

		int myEventAction = pSceneTouchEvent.getAction();
		float X = pSceneTouchEvent.getX();
		float Y = pSceneTouchEvent.getY();

		if (myEventAction == TouchEvent.ACTION_DOWN) {
			
			downedX = X;
			downedY = Y;
			timer = 0;
		}

		if (myEventAction == TouchEvent.ACTION_UP) {
			
			upedX = X;
			upedY = Y;

			// CONSTANTS
			//Sensibility to detect a Zoom
			float ZOOMSENSIBILITY = 15;
			//Sensibility of the scroll detection
			float TOUCHSENSIBILITY = 5;
			if ((upedX < (downedX - ZOOMSENSIBILITY))
					|| (upedX > (downedX + ZOOMSENSIBILITY))
					|| (upedY < (downedY - ZOOMSENSIBILITY))
					|| (upedY > (downedY + ZOOMSENSIBILITY))
					|| (timer > TOUCHSENSIBILITY)) {
				// ---Detect that a zoom or scroll was applied
			} else {
				// PUT THE CURSOR INTO THE ZONE SELECTED
				popup.setPosition(X - popup.getWidth() / 2,
						Y - popup.getHeight() / 2);

				popup.testZone(ArrayTowerAlly,ArrayWall, scene);

			}

		}

		return true;
	}

	//THIS METHOD MANAGE THE TMX FILE
	public void managementtmx() {
		try {
			final TMXLoader tmxLoader = new TMXLoader(getAssets(),
					getTextureManager(), TextureOptions.NEAREST,
					getVertexBufferObjectManager(),
					new ITMXTilePropertiesListener() {
						@Override
						public void onTMXTileWithPropertiesCreated(
								final TMXTiledMap tmxTiledMap,
								final TMXLayer tmxLayer,
								final TMXTile tmxTile,
								final TMXProperties<TMXTileProperty> tmxTileProperties) {


						}
					});

			this.tiledMap = tmxLoader.loadFromAsset("tmx/forest_map.tmx"); //LOAD THE FILE

			// ----------------DETECTION OF THE COLLISIONS AND THE WAYS
			for (final TMXObjectGroup group : tiledMap.getTMXObjectGroups()) {
				if (group.getTMXObjectGroupProperties().containsTMXProperty(
						"wall", "true")) {
					// It detects the wall placed on the map.
					for (final TMXObject object : group.getTMXObjects()) {
						ArrayWall.add(new Rectangle(object.getX(), object
								.getY(), object.getWidth(), object.getHeight(),
								getVertexBufferObjectManager()));

					}
				}

				if (group.getTMXObjectGroupProperties().containsTMXProperty(
						"way", "true")) {
					// It detects the way placed on the map...It also detect the
					// direction to take on this way
					for (final TMXObject object : group.getTMXObjects()) {
						if (object.getTMXObjectProperties()
								.containsTMXProperty("dir", "up")) {
							ArrayWay.add(new Way(object.getX(), object.getY(),
									object.getWidth(), object.getHeight(),
									getVertexBufferObjectManager(), 1));

						} else if (object.getTMXObjectProperties()
								.containsTMXProperty("dir", "down")) {
							ArrayWay.add(new Way(object.getX(), object.getY(),
									object.getWidth(), object.getHeight(),
									getVertexBufferObjectManager(), 2));

						} else if (object.getTMXObjectProperties()
								.containsTMXProperty("dir", "left")) {
							ArrayWay.add(new Way(object.getX(), object.getY(),
									object.getWidth(), object.getHeight(),
									getVertexBufferObjectManager(), 3));

						} else {
							ArrayWay.add(new Way(object.getX(), object.getY(),
									object.getWidth(), object.getHeight(),
									getVertexBufferObjectManager(), 4));

						}
					}
				}

				if (group.getTMXObjectGroupProperties().containsTMXProperty(
						"castle", "true")) {
					// Detect the castles
					for (final TMXObject object : group.getTMXObjects()) {
						ArrayCastle.add(new Rectangle(object.getX(), object
								.getY(), object.getWidth(), object.getHeight(),
								getVertexBufferObjectManager()));

					}
				}

				if (group.getTMXObjectGroupProperties().containsTMXProperty(
						"spawn", "true")) {
					// Detect the spawn points
					for (final TMXObject object : group.getTMXObjects()) {
						ArraySpawn
								.add(new Sprite(object.getX(), object.getY(),
										this.mHeadbone,
										getVertexBufferObjectManager()));
						ArraySpawn.get(ArraySpawn.size() - 1).setWidth(64);
						ArraySpawn.get(ArraySpawn.size() - 1).setHeight(64);

					}
				}

			}

			scene.setBackground(new Background(0, 0, 0));

			this.tmxLayer = this.tiledMap.getTMXLayers().get(0); // the 0 is
																	// just an
																	// index of
																	// the
																	// layer. It
																	// depends
																	// on how
																	// many
																	// layers
																	// you
																	// created
																	// within
																	// Tiled

			// -----------END OF THE DETECTION COLLISION + WAY
		} catch (final TMXLoadException e) {
			Debug.e(e);
		}

	}

	public void Sceneupdate() {

		//while(!socketManager.isConnected());
		

		// Attach on the scene the spawnpoints
		for (int spawn = 0; spawn < ArraySpawn.size(); spawn++) {
			scene.attachChild(ArraySpawn.get(spawn));
			Log.i("Spawn", "Spawn attached !");

		}
		scene.attachChild(popup); //PUT THE MENU
		
		// Loop of the game
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void onUpdate(float pSecondsElapsed) {


				
				if (true) {
					// Reset of the menu
					scene.detachChild(woodBxMenu);
					scene.attachChild(woodBxMenu);

					// Blink of the spawns points
					if (!ArraySpawn.isEmpty()) {
						for (int blinkspawn = 0; blinkspawn < ArraySpawn.size(); blinkspawn++) {
							ArraySpawn.get(blinkspawn).setAlpha(blink);

						}
						if (!blinkminus) {
							blink = blink + (float) 0.006;

							if (blink > (float) 0.6) {
								blinkminus = true;
							}
						} else {
							blink = blink - (float) 0.006;

							if (blink < (float) 0.05) {
								blinkminus = false;
							}
						}
					}

					//Timer of monsters
        	timerwave++;
        	if(timerwave>TIMER_WAVE)
        	{
        	timerbetwennmonster++;
        	if(timerbetwennmonster>TIMER_MONSTER)
        	{
        		if(!ArraySpawn.isEmpty())
        		{
        				ArrayEnemy.add(new Enemy(ArraySpawn.get(spawnumber).getX(), ArraySpawn.get(spawnumber).getY(),32,32, regWolf, getVertexBufferObjectManager(),scene,ArrayEnemy,50,true,true));

        				// ArrayAlly.add(new Enemy(ArraySpawn.get(spawnumber).getX(), ArraySpawn.get(spawnumber).getY(),32,32, regWolf, getVertexBufferObjectManager(),scene,ArrayAlly,50,info.isGroupOwner,false));

        			monsterpop++;
        			timerbetwennmonster=0;
        		}
  		
        		if(monsterpop>=monsterpopmax)
        		{monsterpop=0;
        			timerwave=0;
        			spawnumber++;
        			if(spawnumber>=ArraySpawn.size())
        			{
        				spawnumber=0;
        				monsterpopmax++;
        			}
        		}
        	}
        	
        	}

					// Enemy move and attack sequence loop
					if (!ArrayEnemy.isEmpty()) {
						for (int enn = 0; enn < ArrayEnemy.size(); enn++) {

							if (ArrayEnemy.get(enn).Move(ArrayWay, ArrayCastle,
									scene)) {
								ArrayEnemy.remove(enn);
								castlelife--;
							} else if (ArrayEnemy.get(enn).attackProjectile(
									scene)) {
								ArrayEnemy.remove(enn);
							}
						}

					}

					if (!ArrayAlly.isEmpty()) {
						for (int enn = 0; enn < ArrayAlly.size(); enn++) {

							if (ArrayAlly.get(enn).Move(ArrayWay, ArrayCastle,
									scene)) {
								ArrayAlly.remove(enn);
								castlelife--;
							} else if (ArrayAlly.get(enn).attackProjectile(
									scene)) {
								ArrayAlly.remove(enn);
							}
						}

					}

					// Enemy detection and tower loop
					if (!ArrayTowerAlly.isEmpty()) {
						for (int tow = 0; tow < ArrayTowerAlly.size(); tow++) {
							ArrayTowerAlly.get(tow).preparenewattack();
							ArrayTowerAlly.get(tow).AttackEnnemy(ArrayEnemy,
									scene);
						}

					}

					// Enemy detection and tower loop
					/*if (!ArrayTowerEnemy.isEmpty()) {
						for (int tow = 0; tow < ArrayTowerEnemy.size(); tow++) {
							ArrayTowerEnemy.get(tow).preparenewattack();
							ArrayTowerEnemy.get(tow).AttackEnnemy(ArrayAlly,
									scene);
						}

					}*/

				} else {
					// GAMEOVER

				}

			}

			@Override
			public void reset() {

			}
		});

	}

}