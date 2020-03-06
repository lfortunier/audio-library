package com.ipiecoles.java.audio.controller;

import com.ipiecoles.java.audio.exception.ConflictException;
import com.ipiecoles.java.audio.model.Artist;
import com.ipiecoles.java.audio.repository.AlbumRepository;
import com.ipiecoles.java.audio.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping(value = "/artists")
public class ArtistController {
    public static final Integer PAGE_SIZE_MIN = 10;
    public static final Integer PAGE_SIZE_MAX = 100;
    public static final Integer PAGE_MIN = 0;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Artist getArtist(@PathVariable Long id) {
        return getAndCheckArtistById(id);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, params = {
            "name"
    })
    public Set<Artist> getArtists(@RequestParam("name") String name) {
        return artistRepository.findByNameContainsIgnoreCase(name);
    }

    @RequestMapping(value = "", method = RequestMethod.GET, params = {
            "page",
            "size",
            "sortProperty",
            "sortDirection"
    })
    public Page<Artist> getArtists(@RequestParam(name = "name", required = false) String name,
                                   @RequestParam(name = "page") Integer page,
                                   @RequestParam(name = "size") Integer size,
                                   @RequestParam(name = "sortProperty") String sortProperty,
                                   @RequestParam(name = "sortDirection") Sort.Direction sortDirection) {
        if (page == null) {
            page = PAGE_MIN;
        } else if (page < 0) {
            throw new IllegalArgumentException("Le numéro de page ne peut être inférieur à 0");
        }

        if (size == null) {
            size = PAGE_SIZE_MIN;
        } else if (size < 0 || size > PAGE_SIZE_MAX) {
            throw new IllegalArgumentException("La taille de la page doit être comprise entre 1 et " + PAGE_SIZE_MAX);
        }

        Pageable pageable = PageRequest.of(page, size, sortDirection, sortProperty);
        Page<Artist> employes = artistRepository.findAll(pageable);
        if (page >= employes.getTotalPages()) {
            throw new IllegalArgumentException("Le numéro de page ne peut être supérieur à " + employes.getTotalPages());
        } else if (employes.getTotalElements() == 0) {
            throw new EntityNotFoundException("Il n'y a aucun artistes dans la base de données");
        }
        if (name == null){
            name = "";
        }
        return artistRepository.findByNameContainsIgnoreCase(name, pageable);
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Artist createArtist(@RequestBody Artist artist) throws ConflictException {
        checkArtistName(artist, null);
        return artistRepository.save(artist);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT)
    public Artist setArtist(@PathVariable Long id, @RequestBody Artist artist) throws ConflictException {
        Artist oldArtist = getAndCheckArtistById(id);
        checkArtistName(artist, oldArtist);
        return artistRepository.save(artist);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteArtist(@PathVariable Long id) {
        Artist artist = getAndCheckArtistById(id);
        albumRepository.findByArtist(artist).forEach(album -> albumRepository.delete(album));
        artistRepository.delete(artist);
    }

    private Artist getAndCheckArtistById(Long id) {
        Optional<Artist> artistOptional = artistRepository.findById(id);
        if (artistOptional.isPresent()) {
            return artistOptional.get();
        }
        throw new EntityNotFoundException("L'artiste avec l'id :" + id + " n'a pas été trouvé");
    }

    private void checkArtistName(Artist newArtist, Artist oldArtist) throws ConflictException {
        Optional<Artist> artistOptional = artistRepository.findByName(newArtist.getName());
        if (artistOptional.isPresent()) {
            if (oldArtist == null || !oldArtist.getId().equals(artistOptional.get().getId())) {
                throw new ConflictException("L'artiste avec le nom " + newArtist.getName() + " existe déja");
            }
        }
    }

}
