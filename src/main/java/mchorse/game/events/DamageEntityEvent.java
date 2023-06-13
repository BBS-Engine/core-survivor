package mchorse.game.events;

import mchorse.game.domain.entities.LivingEntity2D;

public class DamageEntityEvent
{
    public final LivingEntity2D entity;
    public final float damage;

    public DamageEntityEvent(LivingEntity2D entity, float damage)
    {
        this.entity = entity;
        this.damage = damage;
    }
}