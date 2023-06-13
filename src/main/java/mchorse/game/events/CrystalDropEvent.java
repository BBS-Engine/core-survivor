package mchorse.game.events;

import mchorse.game.domain.entities.LivingEntity2D;

public class CrystalDropEvent
{
    public final LivingEntity2D entity;

    public CrystalDropEvent(LivingEntity2D entity)
    {
        this.entity = entity;
    }
}