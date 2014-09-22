class Arith
{
    public static void main(String[] args)
    {
	System.out.println(new Ar().compute());
    }
}

class Ar
{
    public int compute()
    {
	int a;
	int b;
	int c;

	a = 1;
	b = 2;
	c = 3;

	return ((a*b)+(b*c))/(a-b);

    }
}
