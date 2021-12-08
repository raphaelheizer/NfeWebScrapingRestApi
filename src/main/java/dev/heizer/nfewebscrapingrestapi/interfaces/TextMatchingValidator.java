package dev.heizer.nfewebscrapingrestapi.interfaces;


public abstract class TextMatchingValidator<T>
{
    protected final String pattern;
    protected T expectedResult;
    protected String text;

    public TextMatchingValidator(String pattern, T expectedResult)
    {
        this.pattern = pattern;
        this.expectedResult = expectedResult;
    }

    public abstract T validate();

    public void setTextToValidate(String text) {this.text = text;}
}
