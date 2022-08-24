package src.main.java.com.example.datastructure.base.hastab;

public class HashTableDemo {

    public static void main(String[] args) {

    }

    class Emp {
        public int id;

        public String name;

        public Emp next;

        public Emp(int id, String name, Emp next) {
            this.id = id;
            this.name = name;
            this.next = next;
        }
    }
}
