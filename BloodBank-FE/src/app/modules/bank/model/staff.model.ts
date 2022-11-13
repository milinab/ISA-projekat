import { Address} from "./address.model";

export class Staff {
  id: number = 0;
  username: string = '';
  firstName: string = '';
  lastName: string = '';
  email: string = '';
  address: Address = new Address();
  phoneNumber: string = '';
  umcn: string = '';
  gender: string = '';
  institution: string = '';


  public constructor(obj?: any) {
    if (obj) {
      this.id = obj.id;
      this.username = obj.username;
      this.firstName = obj.firstName;
      this.lastName = obj.lastName;
      this.email = obj.email;
      this.address = obj.address;
      this.phoneNumber = obj.phoneNumber;
      this.umcn = obj.umcn;
      this.gender = obj.gender;
      this.institution = obj.institution;
    }
  }
}