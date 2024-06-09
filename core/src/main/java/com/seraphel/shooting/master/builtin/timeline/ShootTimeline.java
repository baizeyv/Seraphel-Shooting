package com.seraphel.shooting.master.builtin.timeline;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.seraphel.shooting.SeraphelGame;
import com.seraphel.shooting.actor.SpriteActor;
import com.seraphel.shooting.base.BaseScreen;
import com.seraphel.shooting.master.Utils;

/**
 * ShootTimeline 用 EventTimeline 感觉更好一点
 */
public class ShootTimeline extends BaseTimeline {

    public ShootTimeline(int frameCount) {
        super(frameCount);
    }

    public void setFrame(int frameIndex, float time) {
        frames[frameIndex] = time;
        originFrames[frameIndex] = time;
    }

    @Override
    public void call(float lastTime, float time) {
        super.call(lastTime, time);
        float[] frames = this.frames;
        int frameCount = frames.length;
        if (lastTime > time) { // 对于循环播放的来说,当前帧是播放一轮之后开始播放下一轮的开始帧
            if (frames[frameCount - 1] > 0) {
                shoot(time);
                frames[frameCount - 1] = -1;
            }
            System.out.println("-----------------------------------------------------");
            restore();
            lastTime = -1;
        } else if (lastTime >= frames[frameCount - 1]) { // 上一帧的时间在最后时刻之后, 能触发此事件的一定为不循环播放的
            if (frames[frameCount - 1] > 0) {
                shoot(time);
                frames[frameCount - 1] = -1;
            }
            return;
        }

        if (time < frames[0]) // 当前时间在最早时刻之前
            return;

        int frame;
        if (lastTime < frames[0]) {
            if (frames[0] == 0) {
                shoot(time);
            }
            frame = 0;
        } else {
            frame = Utils.binarySearch(frames, lastTime);
            float frameTime = frames[frame];
            if (frames[frame - 1] > 0) {
                // TODO: shoot
                shoot(time);
                frames[frame - 1] = -1;
            }
        }
    }

    @Override
    public void verify(float lastTime, float time, boolean loop) {
        super.verify(lastTime, time, loop);
        if (!loop) {
            float[] frames = this.frames;
            int frameCount = frames.length;
            if (frames[frameCount - 1] > 0) {
                shoot(time);
                frames[frameCount - 1] = -1;
            }
        }
    }

    private void shoot(float time) {
        Screen scr = SeraphelGame.ins.getScreen();
//        System.out.println("Shoot - " + TimeUtils.millis() + "  " + time);
        if (scr instanceof BaseScreen) {
            SpriteActor spriteActor = new SpriteActor("test.plist", "border");
            spriteActor.addAction(Actions.sequence(
                Actions.moveBy(0, 500, 2),
                Actions.removeActor()
            ));
            spriteActor.setPosition(100, 200);
            ((BaseScreen) scr).addActor(spriteActor);
        }
    }

    @Override
    public void restore() {
        super.restore();
    }
}
