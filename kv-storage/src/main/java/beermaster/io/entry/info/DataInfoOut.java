package beermaster.io.entry.info;

import beermaster.data.InfoRecord;

import java.io.IOException;

public interface DataInfoOut {
    DataInfoOut write(InfoRecord infoRecord) throws IOException;
    DataInfoOut create() throws IOException;
    DataInfoOut delete();
}
