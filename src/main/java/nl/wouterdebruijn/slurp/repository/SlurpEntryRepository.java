package nl.wouterdebruijn.slurp.repository;

import nl.wouterdebruijn.slurp.api.SlurpAPI;
import nl.wouterdebruijn.slurp.controller.LogController;
import nl.wouterdebruijn.slurp.entity.SlurpEntry;
import nl.wouterdebruijn.slurp.exceptions.APIPostException;

import java.net.http.HttpResponse;

public class SlurpEntryRepository {
    public static void save(SlurpEntry entry) throws APIPostException {
        try {
            HttpResponse<String> response = SlurpAPI.post("entry", entry);

            // TODO: Update local storage
        } catch (Exception e) {
            LogController.error("Could not save Slurp Entry!");
            throw new APIPostException();
        }
    }
}
