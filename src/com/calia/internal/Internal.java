package com.calia.internal;

import com.calia.CaliaBase;
import com.calia.object.Entity;

public class Internal {
    CaliaBase caliaBase;

    public void registerEntity(Entity entity) {
        //caliaBase.getEntityRist
    }

    public boolean isCaliaBaseNull() {
        return caliaBase == null;
    }

    public void registerBase(CaliaBase caliaBase) {
        this.caliaBase = caliaBase;
    }
}
