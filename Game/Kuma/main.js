enchant();

window.onload = function(){

    var core = new Core(320,320);
    core.preload('chara1.png');
    core.fps = 15;

    var score = 0;

    core.onload = function(){
        var bear = new Sprite(32,32);
        bear.x = rand(320);
        bear.y = rand(320);
        bear.frame = 0;
        bear.image = core.assets['chara1.png'];
        
        bear.on('touchstart',function(){
            score++;
            scoreLabel.text = 'Score: ' + score;
            this.x = rand(320);
            this.y = rand(320);
        })

        var scoreLabel = new Label('Score: 0');
        scoreLabel.x = 200;
        scoreLabel.y = 5;

        core.rootScene.addChild(scoreLabel);
        core.rootScene.addChild(bear);
    }

    core.start();

}

function rand(n){
    return Math.floor(Math.random()*(n+1));
}