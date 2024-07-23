package pt.isec.pa.javalife.model.data;

public record Area(double xi, double yi, double yf, double xf) {
    public boolean contains(double x, double y) {
        return  x >= this.xi &&
                x <= this.xi + this.xf - 1 &&
                y >= this.yi &&
                y <= this.yi + this.yf - 1;
    }

    public boolean contains(Area other) {
        return this.xi < other.xi + other.xf &&
                this.xi + this.xf > other.xi &&
                this.yi < other.yi + other.yf &&
                this.yi + this.yf > other.yi;
    }
}