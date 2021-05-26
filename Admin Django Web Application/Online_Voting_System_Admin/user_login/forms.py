from django import forms
from django.contrib.auth.forms import UserCreationForm
from django.contrib.auth.models import User
from bootstrap3_datepicker.widgets import DatePickerInput

class NewUserForm(UserCreationForm):
    email=forms.EmailField(required=True)
    class Meta:
        model=User
        fields=('username','email','password1','password2')
    def save(self, commit=True):
        user=super(NewUserForm,self).save(commit=False)
        user.email=self.cleaned_data['email']
        if commit:
            user.save()
        return user

STATE_CHOICES=[('Andaman Nicobar', 'Andaman Nicobar'),
 ('Andhra Pradesh', 'Andhra Pradesh'),
 ('Arunachal Pradesh', 'Arunachal Pradesh'),
 ('Assam', 'Assam'),
 ('Bihar', 'Bihar'),
 ('Chandigarh', 'Chandigarh'),
 ('Chhattisgarh', 'Chhattisgarh'),
 ('Dadra Nagar Haveli', 'Dadra Nagar Haveli'),
 ('Daman Diu', 'Daman Diu'),
 ('Delhi', 'Delhi'),
 ('Goa', 'Goa'),
 ('Gujarat', 'Gujarat'),
 ('Haryana', 'Haryana'),
 ('Himachal Pradesh', 'Himachal Pradesh'),
 ('Jammu Kashmir', 'Jammu Kashmir'),
 ('Jharkhand', 'Jharkhand'),
 ('Karnataka', 'Karnataka'),
 ('Kerala', 'Kerala'),
 ('Ladakh', 'Ladakh'),
 ('Lakshadweep', 'Lakshadweep'),
 ('Madhya Pradesh', 'Madhya Pradesh'),
 ('Maharashtra', 'Maharashtra'),
 ('Manipur', 'Manipur'),
 ('Meghalaya', 'Meghalaya'),
 ('Mizoram', 'Mizoram'),
 ('Nagaland', 'Nagaland'),
 ('Odisha', 'Odisha'),
 ('Puducherry', 'Puducherry'),
 ('Punjab', 'Punjab'),
 ('Rajasthan', 'Rajasthan'),
 ('Sikkim', 'Sikkim'),
 ('Tamil Nadu', 'Tamil Nadu'),
 ('Telangana', 'Telangana'),
 ('Tripura', 'Tripura'),
 ('Uttarakhand', 'Uttarakhand'),
 ('Uttar Pradesh', 'Uttar Pradesh'),
 ('West Bengal', 'West Bengal')]
class voters_form(forms.Form):
    name = forms.CharField(label="Voter Name:")
    phone = forms.CharField(label="Phone No:")
    state = forms.CharField(label="State",widget=forms.Select(choices=STATE_CHOICES))
    district=forms.CharField(label="District",widget=forms.Select(choices=[]))
    legislative_assembly=forms.CharField(label="Legislative Assembly",widget=forms.Select(choices=[]))
    image = forms.ImageField()
    dob = forms.DateField(widget = forms.TextInput(attrs={'placeholder': 'DD/MM/YYYY'} ))




