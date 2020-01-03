package com.ipiecoles.java.audio;

import com.ipiecoles.java.audio.model.Album;
import com.ipiecoles.java.audio.model.Artist;
import com.ipiecoles.java.audio.repository.AlbumRepository;
import com.ipiecoles.java.audio.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MyRunner implements CommandLineRunner {

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Override
    public void run(String... args) throws Exception {
        Optional<Album> albumOpt = albumRepository.findAlbumByTitle("Big Ones");
        System.out.println(albumOpt.get().getId());
        Page<Artist> artistPage = artistRepository.findByNameContainsIgnoreCase("ac", PageRequest.of(0,10, Sort.Direction.ASC, "name"));

        artistPage.get().forEach(artist -> System.out.println(artist.getName()));
    }
}
