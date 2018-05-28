/*
 * Copyright (c) Patrick Magauran 2018.
 *   Licensed under the AGPLv3. All conditions of said license apply.
 *       This file is part of ABOS.
 *
 *       ABOS is free software: you can redistribute it and/or modify
 *       it under the terms of the GNU Affero General Public License as published by
 *       the Free Software Foundation, either version 3 of the License, or
 *       (at your option) any later version.
 *
 *       ABOS is distributed in the hope that it will be useful,
 *       but WITHOUT ANY WARRANTY; without even the implied warranty of
 *       MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *       GNU Affero General Public License for more details.
 *
 *       You should have received a copy of the GNU Affero General Public License
 *       along with ABOS.  If not, see <http://www.gnu.org/licenses/>.
 */

package Utilities;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class Settable<T> {
    private T val;
    private T inval;
    private Boolean set;

    public Settable(T val, T inval) {

        this.val = val;
        this.inval = inval;
        set = (!Objects.equals(val, inval));
    }

    public Settable(T val) {
        this.val = val;
        this.inval = null;
        set = false;
    }

    public Settable() {
        val = null;
        this.inval = null;
        set = false;
    }

    public Boolean isSet() {
        return set;
    }

    public void set(T val) {
        if (!(val == null)) {
            this.val = val;
            set = (!Objects.equals(val, inval));
        } else {
            set = false;
        }

    }

    public void setIfNot(T val) {
        if (!set) {
            this.val = val;
            set = true;
        }
    }

    public T get() {
        return isSet() ? val : inval;
    }

    public void clear() {
        this.val = null;
        set = false;
    }

    public void ifSet(Consumer<? super T> consumer) {
        if (this.set && !Objects.equals(val, inval)) {
            consumer.accept(this.val);
        }
    }

    public T orElseGetAndSet(Supplier<? extends T> var1) {
        if (!(this.set && !Objects.equals(val, inval))) {
            this.val = var1.get();
        }
        return this.val;
    }

    public T orElseGet(Supplier<? extends T> var1) {
        return (this.set && !Objects.equals(val, inval)) ? this.val : var1.get();
    }

    public String toString() {
        return val.toString();
    }
}

