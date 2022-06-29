package com.dabomstew.pkrandom.romhandlers;

/*----------------------------------------------------------------------------*/
/*--  AbstractSwitchRomHandler.java - a base class for Switch rom handlers  --*/
/*--                                  which standardizes common Switch      --*/
/*--                                  functions.                            --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer ZX" by the UPR-ZX team          --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2020.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import com.dabomstew.pkrandom.exceptions.RandomizerIOException;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;

public abstract class AbstractSwitchRomHandler extends AbstractRomHandler {

    private String loadedFN;

    public AbstractSwitchRomHandler(Random random, PrintStream logStream) {
        super(random, logStream);
    }

    @Override
    public boolean loadRom(String filename) {
        if (!this.detectSwitchGame(filename)) {
            return false;
        }
        this.loadedROM(filename);
        loadedFN = filename;
        return true;
    }

    protected abstract boolean detectSwitchGame(String filePath);

    @Override
    public String loadedFilename() {
        return loadedFN;
    }

    protected abstract void loadedROM(String filename);

    protected abstract void savingROM() throws IOException;

    protected abstract String getGameAcronym();

    @Override
    public boolean saveRomFile(String filename, long seed) {
        try {
            savingROM();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return true;
    }

    @Override
    public boolean saveRomDirectory(String filename) {
        try {
            savingROM();
        } catch (IOException e) {
            throw new RandomizerIOException(e);
        }
        return true;
    }

    protected abstract boolean isGameUpdateSupported(int version);

    @Override
    public boolean hasGameUpdateLoaded() {
        return false;
    }

    @Override
    public boolean loadGameUpdate(String filename) {
        return true;
    }

    @Override
    public void removeGameUpdate() {
    }

    protected abstract String getGameVersion();

    @Override
    public String getGameUpdateVersion() {
        return getGameVersion();
    }

    @Override
    public void printRomDiagnostics(PrintStream logStream) {
    }

    @Override
    public boolean hasPhysicalSpecialSplit() {
        // Default value for Gen4+.
        // Handlers can override again in case of ROM hacks etc.
        return true;
    }
}
