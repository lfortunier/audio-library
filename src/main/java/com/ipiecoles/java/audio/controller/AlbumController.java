package com.ipiecoles.java.audio.controller;

import com.ipiecoles.java.audio.exception.ConflictException;
import com.ipiecoles.java.audio.model.Album;
import com.ipiecoles.java.audio.repository.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityNotFoundException;
import java.util.Optional;

@RestController
@RequestMapping(value = "/albums")
public class AlbumController {

    @Autowired
    private AlbumRepository albumRepository;

    @RequestMapping(value = "", method = RequestMethod.POST)
    public Album deleteAlbum(@RequestBody Album album) throws ConflictException {
        checkAlbumValues(album);
        return albumRepository.save(album);
    }

    @RequestMapping(value = "{id}", method = RequestMethod.DELETE)
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteAlbum(@PathVariable Long id) {
        albumRepository.delete(getAndCheckAlbum(id));
    }

    private Album getAndCheckAlbum(Long id) {
        Optional<Album> albumOptional = albumRepository.findById(id);
        if (albumOptional.isPresent()){
            return albumOptional.get();
        }
        throw new EntityNotFoundException("L'album avec l'id :" + id + " n'a pas été trouvé");
    }

    private void checkAlbumValues(Album album) throws ConflictException {
        if (album.getTitle() != null && !album.getTitle().equals("")) {
            Optional<Album> albumOptionalName = albumRepository.findAlbumByTitleAndArtist(album.getTitle(), album.getArtist());
            if (albumOptionalName.isPresent()) {
                throw new ConflictException("L'album avec le titre " + albumOptionalName.get().getTitle() + " est deja dans la liste de l'artiste " + albumOptionalName.get().getArtist().getName());
            }
        }else {
            throw new IllegalArgumentException("l'album ne doit pas avoir un tire vide");
        }
    }
}
