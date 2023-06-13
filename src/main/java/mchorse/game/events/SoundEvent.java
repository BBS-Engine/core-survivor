package mchorse.game.events;

import mchorse.bbs.resources.Link;

public class SoundEvent
{
    public final Link sound;
    public final float volume;

    public SoundEvent(Link sound, float volume)
    {
        this.sound = sound;
        this.volume = volume;
    }
}