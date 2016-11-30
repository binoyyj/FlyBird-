package com.binoy.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;


import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture backgroundImg;
    Texture toptube;
    Texture bottomtube;
    Texture funnybird;
    Texture playbutton;
    //ShapeRenderer shapeRenderer;

   // Texture[] birds;
    Texture zbird;
    int flapstate=0;
	float birdY=0;
    float velocity=0;
    Circle birdCircle;
    Rectangle[] toprectangles;
    Rectangle[] bottomrectangles;

    int gameState=0;
    float gap=600;
    float maxoffset;
    Random randomno;

    int numberOfTubes=4;
    int []tubeX=new int[numberOfTubes];
    float []tubeoffset=new float[numberOfTubes];
    int tubeVelocity=13;
    int distanceBetweenTubes;
    int score=0;
    int scoringTube=0;
    BitmapFont bmf;
    BitmapFont developerText;
    BitmapFont highscore;
    Music music;
    float accelX,accelY,accelZ;
    Preferences prefs;

	@Override
	public void create () {
		batch = new SpriteBatch();
        backgroundImg=new Texture("bg.png");
        //dead = Gdx.audio.newSound(Gdx.files.internal("dead.mp3"));
        funnybird=new Texture("gameover.png");
        //playbutton=new Texture("playbtn.png");
        //shapeRenderer=new ShapeRenderer();
        birdCircle=new Circle();
        toprectangles=new Rectangle[numberOfTubes];
        bottomrectangles=new Rectangle[numberOfTubes];
        bmf=new BitmapFont();
        bmf.setColor(Color.WHITE);
        bmf.getData().setScale(10);

        developerText=new BitmapFont();
        developerText.setColor(Color.WHITE);
        developerText.getData().setScale(3);

        highscore=new BitmapFont();
        highscore.setColor(Color.YELLOW);
        highscore.getData().setScale(10);

        //birds=new Texture[2];
        //birds[0]=new Texture("bird.png");
        //birds[1]=new Texture("bird2.png");
        zbird=new Texture("zbird.jpg");
        birdY=Gdx.graphics.getHeight()/2-zbird.getHeight()/2;
        toptube=new Texture("toptube.png");
        bottomtube=new Texture("bottomtube.png");
        maxoffset=Gdx.graphics.getHeight()/2-gap/2-100;
        randomno=new Random();
        distanceBetweenTubes=Gdx.graphics.getWidth();
        music=Gdx.audio.newMusic(Gdx.files.internal("mario.mp3"));
        music.setLooping(true);
        music.setVolume(0.1f);
        music.play();
        //accelY = Gdx.input.getAccelerometerY();
        //accelZ = Gdx.input.getAccelerometerZ();
        //get a preferences instance
        prefs = Gdx.app.getPreferences("My Preferences");



        //get Integer from preferences, 0 is the default value.

        Gdx.app.log("High Score:", String.valueOf(prefs.getInteger("highscore")));
        startgame();



	}

    public void startgame(){
        birdY=Gdx.graphics.getHeight()/2-zbird.getHeight()/2;

        for(int i=0;i<numberOfTubes;i++){
            tubeoffset[i]=(randomno.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
            tubeX[i]=Gdx.graphics.getWidth()/2-toptube.getWidth()/2+Gdx.graphics.getWidth()+i*distanceBetweenTubes+400;

            toprectangles[i]=new Rectangle();
            bottomrectangles[i]=new Rectangle();
        }
    }

	@Override
	public void render () {

        batch.begin();
        batch.draw(backgroundImg, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        if(gameState==1) {

            if(tubeX[scoringTube]<Gdx.graphics.getWidth()/2)
            {
                score++;
                if(scoringTube<numberOfTubes-1){
                    scoringTube++;
                }else{
                    scoringTube=0;
                }
            }



            if(Gdx.input.isTouched()) {
                velocity = -15;

            }

            for(int i=0;i<numberOfTubes;i++) {

                if(tubeX[i]<-toptube.getWidth()) {
                    tubeX[i]+=numberOfTubes*distanceBetweenTubes;
                    tubeoffset[i]=(randomno.nextFloat()-0.5f)*(Gdx.graphics.getHeight()-gap-200);
                }
                else {
                    tubeX[i] = tubeX[i] - tubeVelocity;
                }
                batch.draw(toptube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeoffset[i]);
                batch.draw(bottomtube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeoffset[i]);

                toprectangles[i]=new Rectangle( tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeoffset[i],toptube.getWidth(),toptube.getHeight());
                bottomrectangles[i]=new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeoffset[i],bottomtube.getWidth(),bottomtube.getHeight());

            }

            if(birdY>0) {
                //velocity++;
               // birdY -= velocity;
               // Gdx.app.log("birdY before:",String.valueOf(birdY));
                accelY = Gdx.input.getAccelerometerY();
                accelY*=6;
                birdY-=accelY;
                Gdx.app.log("birdY after:",String.valueOf(birdY));
            }
            else  {
                gameState=2;
            }
        }else if (gameState==0){

            if(Gdx.input.justTouched()){
                gameState=1;
            }
        }
        else if(gameState==2){
           // batch.draw(playbutton,Gdx.graphics.getWidth()/2-playbutton.getWidth()/2,Gdx.graphics.getHeight()/2-playbutton.getHeight()/2);
            batch.draw(funnybird,Gdx.graphics.getWidth()/2-funnybird.getWidth()/2,Gdx.graphics.getWidth()/2-funnybird.getWidth()/2);
            if(Gdx.input.justTouched()){
                gameState=1;
                startgame();
                score=0;
                scoringTube=0;
                velocity=0;
            }

        }

        batch.draw(zbird, Gdx.graphics.getWidth() / 2 - zbird.getWidth() / 2, birdY);
        bmf.draw(batch,String.valueOf(score),100,200);
        //put some Integer
        if(score>prefs.getInteger("highscore")) {
            prefs.putInteger("highscore", Integer.valueOf(score));
            prefs.flush();
        }

        developerText.draw(batch,"©BinoyJ",Gdx.graphics.getWidth()/2-80,50);
        highscore.draw(batch,String.valueOf(prefs.getInteger("highscore")),Gdx.graphics.getWidth()-200,200);
        batch.end();


        birdCircle.set(Gdx.graphics.getWidth()/2,birdY+zbird.getHeight()/2,zbird.getWidth()/2);

       // shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
       // shapeRenderer.setColor(Color.RED);
        //shapeRenderer.circle(birdCircle.x,birdCircle.y,birdCircle.radius);

        for(int i=0;i<numberOfTubes;i++) {
           // shapeRenderer.rect(tubeX[i],Gdx.graphics.getHeight() / 2 + gap / 2 + tubeoffset[i],toptube.getWidth(),toptube.getHeight());
           // shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomtube.getHeight() + tubeoffset[i],bottomtube.getWidth(),bottomtube.getHeight());

            if(Intersector.overlaps(birdCircle,toprectangles[i])||Intersector.overlaps(birdCircle,bottomrectangles[i])){
                  gameState=2;
            }
        }

       // shapeRenderer.end();
	}

    @Override
    public void dispose() {
        super.dispose();
        music.dispose();
    }
}
