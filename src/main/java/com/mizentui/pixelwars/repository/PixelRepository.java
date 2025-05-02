package com.mizentui.pixelwars.repository;

import com.mizentui.pixelwars.model.Pixel;
import com.mizentui.pixelwars.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

public interface PixelRepository extends CrudRepository<Pixel, Long> {

    List<Pixel> findByAuthor(User author);

    @NativeQuery(value = "SELECT p1.* FROM pixel p1 INNER JOIN (SELECT x, y, MAX(timestamp) AS max_timestamp FROM pixel GROUP BY x, y) AS p2 ON p1.x = p2.x AND p1.y = p2.y AND p1.timestamp = p2.max_timestamp;")
    List<Pixel> getLastPixels();

    @NativeQuery(value = "SELECT user_id FROM pixel GROUP BY user_id ORDER BY COUNT(*) DESC LIMIT :top_count;")
    List<Long> getTopAuthorsId(@Param("top_count") Long count);

    Long countByAuthor(User author);

}
