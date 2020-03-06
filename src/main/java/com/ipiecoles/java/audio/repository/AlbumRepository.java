package com.ipiecoles.java.audio.repository;

import com.ipiecoles.java.audio.model.Album;
import com.ipiecoles.java.audio.model.Artist;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlbumRepository extends PagingAndSortingRepository<Album,Long> {
    Optional<Album> findAlbumByTitleAndArtist(String title, Artist artist);

    List<Album> findByArtist(Artist artist);
}
