package beermaster.mapper;

public interface Mapper<Src, Dst> {
    Dst map(Src source);
}
