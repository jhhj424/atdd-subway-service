package nextstep.subway.path.application;

import nextstep.subway.line.domain.Line;
import nextstep.subway.line.domain.LineRepository;
import nextstep.subway.line.domain.Section;
import nextstep.subway.path.domain.PathFinder;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.path.dto.PathStation;
import nextstep.subway.station.domain.Station;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PathService {
    private final LineRepository lineRepository;

    public PathService(LineRepository lineRepository) {
        this.lineRepository = lineRepository;
    }

    public PathResponse findPath(Long source, Long target) {
        List<Line> lines = lineRepository.findAll();
        List<Section> sections = findSectionsInLines(lines);
        PathFinder pathFinder = new PathFinder(sections);
        PathStation sourceStation = PathStation.of(findStationInLines(source, lines));
        PathStation targetStation = PathStation.of(findStationInLines(target, lines));
        return PathResponse.of(pathFinder.findPath(sourceStation, targetStation), pathFinder.findShortestDistance(sourceStation, targetStation));
    }

    private List<Section> findSectionsInLines(List<Line> lines) {
        return lines.stream()
                .flatMap(line -> line.getSections().stream())
                .collect(Collectors.toList());
    }

    private Station findStationInLines(Long stationId, List<Line> lines) {
        return lines.stream()
                .flatMap(line -> line.getStations().stream())
                .filter(station -> station.getId().equals(stationId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않은 출발역이나 도착역입니다."));
    }
}