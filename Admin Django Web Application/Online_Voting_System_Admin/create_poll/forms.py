from django import forms
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
class Candidate_Form(forms.Form):
    name =forms.CharField(label="Name")
    state=forms.CharField(label="State",widget=forms.Select(choices=STATE_CHOICES,attrs={'class':'state'}))
    district=forms.CharField(label="District",widget=forms.Select(choices=[],attrs={'class':'district'}))
    legislative=forms.CharField(label="Legislative",widget=forms.Select(choices=[],attrs={'class':'legislative'}))
    candidate_image=forms.ImageField()
    candidate_symbol=forms.ImageField()
