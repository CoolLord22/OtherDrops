// OtherDrops - a Bukkit plugin
// Copyright (C) 2011 Robert Sargant, Zarius Tularial, Celtic Minstrel
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.	 See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program.	 If not, see <http://www.gnu.org/licenses/>.

package com.gmail.zariust.otherdrops.data;

import com.gmail.zariust.otherdrops.Log;

import org.bukkit.Material;
import org.bukkit.Note;
import org.bukkit.block.BlockState;
import org.bukkit.block.NoteBlock;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class NoteData implements Data, RangeableData {
    private Note note;

    public NoteData(BlockState state) {
        if (state instanceof NoteBlock)
            note = ((NoteBlock) state).getNote();
    }

    public NoteData(Note tone) {
        note = tone;
    }

    @Override
    public int getData() {
        return note.getId();
    }

    @Override
    public void setData(int d) {
        note = new Note((byte) d);
    }

    @Override
    public boolean matches(Data d) {
        if (!(d instanceof NoteData))
            return false;
        return note.equals(((NoteData) d).note);
    }

    @Override
    public String get(Enum<?> mat) {
        String result = "";
        if (mat == Material.NOTE_BLOCK) {
            result += note.getTone();
            if (note.isSharped())
                result += "#";
            result += note.getOctave();
        }
        return result;
    }

    @Override
    public void setOn(BlockState state) {
        if (!(state instanceof NoteBlock)) {
            Log.logWarning("Tried to change a note block, but no note block was found!");
            return;
        }
        ((NoteBlock) state).setNote(note);
    }

    @Override
    // Note blocks are not entities, so nothing to do here
    public void setOn(Entity entity, Player witness) {
    }

    public static Data parse(String state) throws IllegalArgumentException {
        if (state == null || state.isEmpty())
            return null;
        if (state.startsWith("RANGE"))
            return RangeData.parse(state);
        if (!state.matches("([A-G])(#?)([0-2]?)"))
            return null;
        Note.Tone tone = Note.Tone.valueOf(state.substring(0, 1));
        if (tone == null)
            return null;
        byte octave;
        if (state.matches("..?[0-2]"))
            octave = Byte.parseByte(state.substring(state.length() - 1));
        else
            octave = 1;
        Note note = new Note(octave, tone, state.contains("#"));
        return new NoteData(note);
    }

    @Override
    public int hashCode() {
        // Note doesn't define a hashCode() and is not an enum, so use the note
        // ID instead
        return note == null ? 0 : note.getId();
    }

    @Override
    public Boolean getSheared() {
        // TODO Auto-generated method stub
        return null;
    }
}
